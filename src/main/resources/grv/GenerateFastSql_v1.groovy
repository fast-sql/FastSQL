import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import java.util.regex.Matcher
import java.util.regex.Pattern

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

def getPackageName(File dir) {
    //正则表达式
    String patterString = String.format(".*src%smain%sjava(.*)", File.separator, File.separator)
    Pattern pattern = Pattern.compile(patterString)
    Matcher matcher = pattern.matcher(dir.getPath())
    //判断是否匹配正则表达式
    if (!matcher.find()) {
        //不匹配，抛出异常
        throw new IllegalArgumentException("路径错误，请选择\"src/main/java\"下的子目录")
    } else {
        //匹配
        String packageNameSegment = matcher.group(1)
        //判断目标字符串是否等于""
        if (Objects.equals(packageNameSegment, "")) {
            //等于，则选择路径在项目根目录下，返回空包名
            return EMPTY_PACKAGE_NAME
        } else {
            //不等于，则返回非空包名
            return String.format("package %s;", packageNameSegment.substring(1).replace(File.separator, "."))
        }
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