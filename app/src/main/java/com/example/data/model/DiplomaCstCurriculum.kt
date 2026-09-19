package com.example.data.model

data class PredefinedSubject(
    val subjectName: String,
    val subjectCode: String
)

object DiplomaCstCurriculum {

    val semestersWithSubjects: Map<String, List<PredefinedSubject>> = mapOf(
        "1st Semester" to listOf(
            PredefinedSubject("Bangla 1", "25711"),
            PredefinedSubject("English 1", "25712"),
            PredefinedSubject("Engineering Drawing", "21011"),
            PredefinedSubject("Physics 1", "25912"),
            PredefinedSubject("Mathematics 1", "25911"),
            PredefinedSubject("Basic Electricity", "26711"),
            PredefinedSubject("Computer Office Application", "26611")
        ),
        "2nd Semester" to listOf(
            PredefinedSubject("Bangla 2", "25721"),
            PredefinedSubject("English 2", "25722"),
            PredefinedSubject("Mathematics 2", "25921"),
            PredefinedSubject("Physical Education and Life Skill Development", "25812"),
            PredefinedSubject("Physics 2", "25922"),
            PredefinedSubject("Computer Graphics Design 1", "26622"),
            PredefinedSubject("Python Programming", "26621"),
            PredefinedSubject("Basic Electronics", "26811")
        ),
        "3rd Semester" to listOf(
            PredefinedSubject("Social Science", "25811"),
            PredefinedSubject("Chemistry", "25913"),
            PredefinedSubject("Mathematics 3", "25931"),
            PredefinedSubject("Digital Electronics 1", "26831"),
            PredefinedSubject("Application Development Using Python", "28531"),
            PredefinedSubject("Graphics Design 2", "28532"),
            PredefinedSubject("IT Support Services", "28533")
        ),
        "4th Semester" to listOf(
            PredefinedSubject("Business Communication", "25831"),
            PredefinedSubject("Digital Electronics 2", "26841"),
            PredefinedSubject("Java Programming", "28541"),
            PredefinedSubject("Data Structure & Algorithm", "28542"),
            PredefinedSubject("Computer Peripherals & Interfacing", "28543"),
            PredefinedSubject("Web Design & Development 1", "28544"),
            PredefinedSubject("Environmental Studies", "29041")
        ),
        "5th Semester" to listOf(
            PredefinedSubject("Accounting", "25841"),
            PredefinedSubject("Application Development Using Java", "28551"),
            PredefinedSubject("Web Design & Development 2", "28552"),
            PredefinedSubject("Computer Architecture & Microprocessor", "28553"),
            PredefinedSubject("Data Communication", "28554"),
            PredefinedSubject("Operating System", "28555"),
            PredefinedSubject("Project Work 1", "28556")
        ),
        "6th Semester" to listOf(
            PredefinedSubject("Principles of Marketing", "25851"),
            PredefinedSubject("Industrial Management", "25852"),
            PredefinedSubject("Database Management System", "28561"),
            PredefinedSubject("Computer Networking", "28562"),
            PredefinedSubject("Sensor & IOT System", "28563"),
            PredefinedSubject("Microcontroller Based System Design & Development", "28564"),
            PredefinedSubject("Surveillance Security System", "28565"),
            PredefinedSubject("Web Development Project", "28566")
        ),
        "7th Semester" to listOf(
            PredefinedSubject("Innovation & Entrepreneurship", "25853"),
            PredefinedSubject("Digital Marketing Technique", "28571"),
            PredefinedSubject("Network Administration & Services", "28572"),
            PredefinedSubject("Cyber Security & Ethics", "28573"),
            PredefinedSubject("Apps Development Project", "28574"),
            PredefinedSubject("Multimedia & Animation", "28575"),
            PredefinedSubject("Project Work 2", "28576")
        )
    )

    fun getSubjectsForSemester(semesterName: String): List<PredefinedSubject>? {
        // Try direct match or matching prefix like "1st", "2nd", etc.
        val normalized = semesterName.trim()
        semestersWithSubjects[normalized]?.let { return it }

        for ((key, list) in semestersWithSubjects) {
            val keyPrefix = key.split(" ").firstOrNull() ?: ""
            if (keyPrefix.isNotEmpty() && normalized.startsWith(keyPrefix, ignoreCase = true)) {
                return list
            }
        }
        return null
    }
}
