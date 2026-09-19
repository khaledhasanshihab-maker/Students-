package com.example.data.model

data class DepartmentCurriculum(
    val departmentName: String,
    val departmentCode: String,
    val shortName: String,
    val semestersWithSubjects: Map<String, List<PredefinedSubject>>
)

object DiplomaCurricula {

    val allCurricula: List<DepartmentCurriculum> by lazy {
        listOf(
            computerCurriculum,
            electronicsCurriculum,
            foodCurriculum,
            racCurriculum
        )
    }

    // 1. COMPUTER SCIENCE TECHNOLOGY (CST)
    val computerCurriculum = DepartmentCurriculum(
        departmentName = "Computer Technology",
        departmentCode = "85",
        shortName = "CST",
        semestersWithSubjects = mapOf(
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
    )

    // 2. ELECTRONICS TECHNOLOGY (ENT)
    val electronicsCurriculum = DepartmentCurriculum(
        departmentName = "Electronics Technology",
        departmentCode = "68",
        shortName = "ENT",
        semestersWithSubjects = mapOf(
            "1st Semester" to listOf(
                PredefinedSubject("Engineering Drawing", "21011"),
                PredefinedSubject("Bangla 1", "25711"),
                PredefinedSubject("English 1", "25712"),
                PredefinedSubject("Mathematics 1", "25911"),
                PredefinedSubject("Physics 1", "25912"),
                PredefinedSubject("Basic Electricity", "26711"),
                PredefinedSubject("Basic Electronics", "26811")
            ),
            "2nd Semester" to listOf(
                PredefinedSubject("Bangla 2", "25721"),
                PredefinedSubject("English 2", "25722"),
                PredefinedSubject("Social Science", "25811"),
                PredefinedSubject("Physical Education & Life Skills Development", "25812"),
                PredefinedSubject("Chemistry", "25913"),
                PredefinedSubject("Mathematics 2", "25921"),
                PredefinedSubject("Electrical Circuits 1", "26721"),
                PredefinedSubject("Electronic Devices and Circuits", "26821")
            ),
            "3rd Semester" to listOf(
                PredefinedSubject("Computer Office Application", "28511"),
                PredefinedSubject("Digital Electronics 1", "26831"),
                PredefinedSubject("Electrical Circuits 2", "26731"),
                PredefinedSubject("Mathematics 3", "25931"),
                PredefinedSubject("Physics 2", "25922"),
                PredefinedSubject("Power Electronics", "26832")
            ),
            "4th Semester" to listOf(
                PredefinedSubject("Accounting", "25841"),
                PredefinedSubject("Communication Engineering", "26842"),
                PredefinedSubject("DC Machine", "26742"),
                PredefinedSubject("Digital Electronics 2", "26841"),
                PredefinedSubject("Electrical Installation, Planning & Estimating", "26741"),
                PredefinedSubject("Electronic Servicing", "26844"),
                PredefinedSubject("Networks, Filters and Transmission Lines", "26843")
            ),
            "5th Semester" to listOf(
                PredefinedSubject("Bio-Medical Instruments", "28654"),
                PredefinedSubject("Electrical & Electronic Measurements 1", "26752"),
                PredefinedSubject("Electronic Appliances", "26852"),
                PredefinedSubject("Generation of Electrical Power", "26751"),
                PredefinedSubject("Industrial Management", "25852"),
                PredefinedSubject("Principles of Marketing", "25851"),
                PredefinedSubject("Programming in C", "28567"),
                PredefinedSubject("Television Engineering", "26851")
            ),
            "6th Semester" to listOf(
                PredefinedSubject("AC Machine 1", "26761"),
                PredefinedSubject("Electrical & Electronic Measurements 2", "26763"),
                PredefinedSubject("Microcontroller and Embedded System", "26862"),
                PredefinedSubject("PCB Design and Prototyping", "26863"),
                PredefinedSubject("TV Studio and Broadcasting", "26861"),
                PredefinedSubject("Transmission & Distribution of Electrical Power", "26764")
            ),
            "7th Semester" to listOf(
                PredefinedSubject("AC Machine 2", "26771"),
                PredefinedSubject("Control System and Robotics", "26873"),
                PredefinedSubject("Electronic Project", "26874"),
                PredefinedSubject("Industrial Automation and PLC", "26872"),
                PredefinedSubject("Microwave, Radar and Navigation Aids", "26871")
            )
        )
    )

    // 3. FOOD TECHNOLOGY (FT)
    val foodCurriculum = DepartmentCurriculum(
        departmentName = "Food Technology",
        departmentCode = "69",
        shortName = "FT",
        semestersWithSubjects = mapOf(
            "1st Semester" to listOf(
                PredefinedSubject("Bangla 1", "25711"),
                PredefinedSubject("English 1", "25712"),
                PredefinedSubject("Engineering Drawing", "21011"),
                PredefinedSubject("Mathematics 1", "25911"),
                PredefinedSubject("Physics 1", "25912"),
                PredefinedSubject("Food Engineering Fundamentals", "26911"),
                PredefinedSubject("Food Safety & Hygiene Management", "26912")
            ),
            "2nd Semester" to listOf(
                PredefinedSubject("Bangla 2", "25721"),
                PredefinedSubject("English 2", "25722"),
                PredefinedSubject("Chemistry", "25913"),
                PredefinedSubject("Mathematics 2", "25921"),
                PredefinedSubject("Social Science", "25811"),
                PredefinedSubject("Physical Education & Life Skills Development", "25812"),
                PredefinedSubject("Basic Electricity & Electronics", "26711")
            ),
            "3rd Semester" to listOf(
                PredefinedSubject("Mathematics 3", "25931"),
                PredefinedSubject("Physics 2", "25922"),
                PredefinedSubject("Computer Office Application", "26611"),
                PredefinedSubject("Industrial Stoichiometry & Thermodynamics", "26364"),
                PredefinedSubject("Instrumental Methods of Analysis", "26365"),
                PredefinedSubject("Food Engineering Operation 1", "26961")
            ),
            "4th Semester" to listOf(
                PredefinedSubject("Food Microbiology 1", "26941"),
                PredefinedSubject("Food Preservation 1", "26942"),
                PredefinedSubject("Food Chemistry", "26943"),
                PredefinedSubject("Food Packaging", "26944"),
                PredefinedSubject("Dairy Products", "26945"),
                PredefinedSubject("Engineering Mechanics", "27041"),
                PredefinedSubject("Environmental Studies", "29041")
            ),
            "5th Semester" to listOf(
                PredefinedSubject("Accounting", "25841"),
                PredefinedSubject("Food Microbiology 2", "26951"),
                PredefinedSubject("Food Preservation 2", "26952"),
                PredefinedSubject("Food Biotechnology", "26953"),
                PredefinedSubject("Food & Beverage Products", "26954"),
                PredefinedSubject("Food Industrial Instrumentation & Process Control", "26955"),
                PredefinedSubject("Refrigeration & Cold Storage", "26355")
            ),
            "6th Semester" to listOf(
                PredefinedSubject("Principles of Marketing", "25851"),
                PredefinedSubject("Industrial Management", "25852"),
                PredefinedSubject("Food Process Industries 1", "26962"),
                PredefinedSubject("Bakery Products", "26963"),
                PredefinedSubject("Food Adulteration & Toxicology", "26964"),
                PredefinedSubject("Food Quality Assurance", "26965")
            ),
            "7th Semester" to listOf(
                PredefinedSubject("Innovation & Entrepreneurship", "25853"),
                PredefinedSubject("Food Engineering Operation 2", "26971"),
                PredefinedSubject("Food Process Industries 2", "26972"),
                PredefinedSubject("Food Quality Control & Assurance", "26973"),
                PredefinedSubject("Confectionery Products", "26974"),
                PredefinedSubject("Food Analysis", "26975"),
                PredefinedSubject("Food Engineering Project", "26976")
            )
        )
    )

    // 4. REFRIGERATION AND AIR CONDITIONING TECHNOLOGY (RAC)
    val racCurriculum = DepartmentCurriculum(
        departmentName = "Refrigeration and Air Conditioning Technology",
        departmentCode = "72",
        shortName = "RAC",
        semestersWithSubjects = mapOf(
            "1st Semester" to listOf(
                PredefinedSubject("Bangla 1", "25711"),
                PredefinedSubject("English 1", "25712"),
                PredefinedSubject("Engineering Drawing", "21011"),
                PredefinedSubject("Physics 1", "25912"),
                PredefinedSubject("Mathematics 1", "25911"),
                PredefinedSubject("Basic Workshop Practice", "27011"),
                PredefinedSubject("Refrigeration & AC Fundamentals", "27211")
            ),
            "2nd Semester" to listOf(
                PredefinedSubject("Bangla 2", "25721"),
                PredefinedSubject("English 2", "25722"),
                PredefinedSubject("Mathematics 2", "25921"),
                PredefinedSubject("Physics 2", "25922"),
                PredefinedSubject("Social Science", "25811"),
                PredefinedSubject("Electrical Engineering Fundamentals", "26712"),
                PredefinedSubject("Refrigeration Engineering Drawing", "27221")
            ),
            "3rd Semester" to listOf(
                PredefinedSubject("Mathematics 3", "25931"),
                PredefinedSubject("Chemistry", "25913"),
                PredefinedSubject("Physical Education & Life Skill Development", "25812"),
                PredefinedSubject("Computer Application", "26611"),
                PredefinedSubject("Electronic Engineering Fundamentals", "26822"),
                PredefinedSubject("Refrigeration Cycles & Components", "27231")
            ),
            "4th Semester" to listOf(
                PredefinedSubject("Domestic Refrigeration & Air Conditioning", "27241"),
                PredefinedSubject("Automotive Engines & their Systems", "27242"),
                PredefinedSubject("Cooling & Heating Load Calculation", "27243"),
                PredefinedSubject("Maintenance of RAC Equipment", "27244"),
                PredefinedSubject("Engineering Mechanics", "27041"),
                PredefinedSubject("Environmental Studies", "29041"),
                PredefinedSubject("Business Organization & Communication", "25841")
            ),
            "5th Semester" to listOf(
                PredefinedSubject("Electrical Machines in RAC", "27251"),
                PredefinedSubject("RAC Circuits & Controls", "27252"),
                PredefinedSubject("Piping & Duct Works", "27253"),
                PredefinedSubject("Commercial & Industrial Refrigeration", "27254"),
                PredefinedSubject("Power Plant Engineering", "27151"),
                PredefinedSubject("Engineering Thermodynamics", "27131"),
                PredefinedSubject("Accounting Theory & Practice", "25851")
            ),
            "6th Semester" to listOf(
                PredefinedSubject("Advanced Refrigeration & Air Conditioning", "27261"),
                PredefinedSubject("RAC Plants for Food Processing & Preservation", "27262"),
                PredefinedSubject("RAC Plant Operation", "27263"),
                PredefinedSubject("Low Temperature Refrigeration", "27264"),
                PredefinedSubject("Fluid Mechanics & Machineries", "27162"),
                PredefinedSubject("Industrial Management", "25852")
            ),
            "7th Semester" to listOf(
                PredefinedSubject("Innovation & Entrepreneurship", "25853"),
                PredefinedSubject("RAC System Analysis", "27271"),
                PredefinedSubject("RAC Project", "27272"),
                PredefinedSubject("Troubleshooting & Repairing of RAC Equipment", "27273"),
                PredefinedSubject("Transport Refrigeration & Air Conditioning", "27274"),
                PredefinedSubject("Commercial & Industrial Air Conditioning", "27275"),
                PredefinedSubject("Installation of RAC Plants", "27276")
            )
        )
    )

    /**
     * Finds matching curriculum based on department name.
     */
    fun findCurriculumForDepartment(deptName: String): DepartmentCurriculum? {
        val lower = deptName.lowercase().trim()
        return when {
            lower.contains("computer") || lower.contains("cst") -> computerCurriculum
            lower.contains("electronic") || lower.contains("ent") -> electronicsCurriculum
            lower.contains("food") -> foodCurriculum
            lower.contains("refrigeration") || lower.contains("rac") || lower.contains("air condition") -> racCurriculum
            else -> null
        }
    }

    /**
     * Finds subjects for a specific department and semester.
     */
    fun getSubjectsForDeptAndSemester(deptName: String, semesterName: String): List<PredefinedSubject>? {
        val curriculum = findCurriculumForDepartment(deptName) ?: return null
        val normalizedSem = semesterName.trim()
        curriculum.semestersWithSubjects[normalizedSem]?.let { return it }

        for ((key, list) in curriculum.semestersWithSubjects) {
            val keyPrefix = key.split(" ").firstOrNull() ?: ""
            if (keyPrefix.isNotEmpty() && normalizedSem.startsWith(keyPrefix, ignoreCase = true)) {
                return list
            }
        }
        return null
    }
}
