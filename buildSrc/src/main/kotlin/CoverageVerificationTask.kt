import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class CoverageVerificationTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val xmlReport: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val htmlReport: DirectoryProperty

    @TaskAction
    fun verifyReports() {
        val xml = xmlReport.asFile.get()
        val html = htmlReport.asFile.get()

        check(xml.isFile) { "Missing aggregate Kover XML report: $xml" }
        check(File(html, "index.html").isFile) {
            "Missing aggregate Kover HTML report: ${File(html, "index.html")}"
        }
        check(Regex("covered=\"[1-9][0-9]*\"").containsMatchIn(xml.readText())) {
            "Aggregate Kover XML report has no covered counters: $xml"
        }
    }
}