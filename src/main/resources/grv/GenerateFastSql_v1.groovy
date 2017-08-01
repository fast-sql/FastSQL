import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

//packageName = "cn.com.vdin.picasso.common;"
typeMapping = [
        (~/(?i)int/)               : "Integer",
        (~/(?i)bool|boolean/)      : "Boolean",
        (~/(?i)decimal|real/)      : "BigDecimal",
        (~/(?i)float|double/)      : "Double",
        (~/(?i)datetime|timestamp/): "LocalDateTime",
        (~/(?i)date/)              : "LocalDate",
        (~/(?i)time/)              : "LocalTime",
        (~/(?i)/)                  : "String"
]

EMPTY_PACKAGE_NAME = "EMPTY_PACKAGE_NAME"

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each { generate(it, dir) }
}

def generate(table, dir) {
    String packageName = getPackageName(dir)
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    new File(dir, firstCharUpper(className) + ".java").withPrintWriter { out -> generate(out, table.getName(), packageName, firstCharUpper(className), fields) }
    new File(dir, firstCharUpper(className) + "DAO.java").withPrintWriter { out -> generateDao(out, packageName, firstCharUpper(className) + "DAO", firstCharUpper(className)) }
}

def generate(out, tableName, packageName, className, fields) {
    //判断包名是否不为空包名
    if (!Objects.equals(packageName, EMPTY_PACKAGE_NAME)) {
        //不为空包名
        out.println "$packageName"
        out.println ""
    }
    out.println "import javax.persistence.*;"
    out.println "import java.time.*;"
    out.println "import java.util.*;"
    out.println ""
    out.println ""
    out.println "@Table(name = \"${tableName}\")"
    out.println "public class ${className} {"
    out.println ""
    fields.each() {
        if (it.annos != "") out.println "  ${it.annos}"
        out.println "  private ${it.type} ${it.name};"
    }
    out.println ""
    fields.each() {
        out.println ""
        out.println "  public ${it.type} get${it.name.capitalize()}() {"
        out.println "    return ${it.name};"
        out.println "  }"
        out.println ""
        out.println "  public void set${it.name.capitalize()}(${it.type} ${it.name}) {"
        out.println "    this.${it.name} = ${it.name};"
        out.println "  }"
        out.println ""
    }
    out.println "}"
}

def generateDao(out, packageName, className, entityName) {
    //判断包名是否不为空包名
    if (!Objects.equals(packageName, EMPTY_PACKAGE_NAME)) {
        //不为空包名
        out.println "$packageName"
        out.println ""
    }
    out.println "import com.github.fastsql.dao.*;"
    out.println "import com.github.fastsql.dto.*;"
    out.println "import com.github.fastsql.util.*;"
    out.println "import java.time.*;"
    out.println "import java.util.*;"
    out.println ""
    out.println "public class ${className} extends BaseDAO<${entityName},String> {"
    out.println "//"
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           name : javaName(col.getName(), false),
                           type : typeStr,
                           annos: ""]]
    }
}

def javaName(str, capitalize) {
    def s = str.split(/(?<=[^\p{IsLetter}])/).collect { Case.LOWER.apply(it).capitalize() }
            .join("").replaceAll(/[^\p{javaJavaIdentifierPart}]/, "_")
//    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
    underlineToCamel(s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1])
}

def getPackageName(dir) {
    //路径中包名的上一级路径名，既是前一个片段
    String previousSegment = "java"
    //切分路径
    String[] segments = dir.toString().split(File.separator)
    //“前一个片段”在数组中的索引
    int previousSegmentIndex = -1;
    //遍历数组，获取开始片段的索引
    for (int i = 0; i < segments.length; i++) {
        if (Objects.equals(segments[i], previousSegment)) {
            previousSegmentIndex = i;
        }
    }
    //判断开始片段索引状态
    if (previousSegmentIndex == -1) {
        //路径没有"java"片段
        throw new IllegalArgumentException("路径错误，请选择\"src/main/java\"下的子目录")
    } else if (previousSegmentIndex == segments.length - 1) {
        //"java"片段在路径末尾，则不需要包名
        return EMPTY_PACKAGE_NAME;
    } else {
        //"java"片段不在末尾，拼接包名
        StringBuilder stringBuilder = new StringBuilder("package ");
        for (int i = previousSegmentIndex + 1; i < segments.length - 1; i++) {
            stringBuilder.append(segments[i]).append(".")
        }
        stringBuilder.append(segments.last()).append(";")
        return stringBuilder.toString()
    }
}

def underlineToCamel(String param) {

    int len = param.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
        char c = param.charAt(i);
        if (c == '_') {
            if (++i < len) {
                sb.append(Character.toUpperCase(param.charAt(i)));
            }
        } else {
            sb.append(c);
        }
    }

    sb.toString();
}

def camelToUnderline(String param) {

    int len = param.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
        char c = param.charAt(i);
        if (Character.isUpperCase(c)) {
            sb.append('_');
            sb.append(Character.toLowerCase(c));
        } else {
            sb.append(c);
        }
    }
    String temp = sb.toString();
    if (temp.startsWith("_")) {
        return temp.substring(1);
    }
    temp;
}

def firstCharUpper(str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
}

def firstCharLower(str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1);
}