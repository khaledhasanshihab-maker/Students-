package com.example.data.model

data class PredefinedSubject(
    val subjectName: String,
    val subjectCode: String
)

object DiplomaCstCurriculum {

    val semestersWithSubjects: Map<String, List<PredefinedSubject>>
        get() = DiplomaCurricula.computerCurriculum.semestersWithSubjects

    fun getSubjectsForSemester(semesterName: String): List<PredefinedSubject>? {
        return DiplomaCurricula.getSubjectsForDeptAndSemester("Computer", semesterName)
    }

    fun getSubjectsForDepartmentAndSemester(deptName: String, semesterName: String): List<PredefinedSubject>? {
        return DiplomaCurricula.getSubjectsForDeptAndSemester(deptName, semesterName)
    }
}
