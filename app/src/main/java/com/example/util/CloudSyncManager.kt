package com.example.util

import android.content.Context
import android.widget.Toast
import com.example.data.AppDatabase
import com.example.data.model.Attendance
import com.example.data.model.Department
import com.example.data.model.Marks
import com.example.data.model.Semester
import com.example.data.model.Student
import com.example.data.model.Subject
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object CloudSyncManager {

    /**
     * Checks if Firebase is initialized in the app (requires google-services.json).
     */
    fun isFirebaseAvailable(context: Context): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Backs up all local Room SQLite data to Firebase Firestore under the teacher's profile.
     */
    suspend fun syncLocalToCloud(
        context: Context,
        database: AppDatabase,
        teacherId: Long
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isFirebaseAvailable(context)) {
                return@withContext Result.failure(
                    IllegalStateException("Firebase is not configured yet. Please provide 'google-services.json' in the app directory to enable live Cloud Sync.")
                )
            }

            val firestore = FirebaseFirestore.getInstance()
            val teacherDoc = firestore.collection("teachers").document("teacher_$teacherId")

            val depts = database.departmentDao().getDepartmentsByTeacher(teacherId).first()
            val semList = mutableListOf<Semester>()
            val subList = mutableListOf<Subject>()
            val stuList = mutableListOf<Student>()

            for (dept in depts) {
                val sems = database.semesterDao().getSemestersByDepartmentSync(dept.id)
                semList.addAll(sems)
                for (sem in sems) {
                    subList.addAll(database.subjectDao().getSubjectsByDepartmentAndSemesterSync(dept.id, sem.id))
                    stuList.addAll(database.studentDao().getStudentsByDepartmentAndSemesterSync(dept.id, sem.id))
                }
            }

            val syncData = hashMapOf(
                "last_synced" to System.currentTimeMillis(),
                "departments_count" to depts.size,
                "students_count" to stuList.size,
                "subjects_count" to subList.size
            )

            teacherDoc.set(syncData).await()

            // Upload Departments
            val deptCollection = teacherDoc.collection("departments")
            for (dept in depts) {
                deptCollection.document("dept_${dept.id}").set(
                    mapOf("id" to dept.id, "name" to dept.departmentName)
                ).await()
            }

            // Upload Students
            val stuCollection = teacherDoc.collection("students")
            for (stu in stuList) {
                stuCollection.document("student_${stu.id}").set(
                    mapOf(
                        "id" to stu.id,
                        "name" to stu.studentName,
                        "roll" to stu.rollNumber,
                        "reg" to (stu.registrationNumber ?: ""),
                        "deptId" to stu.departmentId,
                        "semId" to stu.semesterId
                    )
                ).await()
            }

            Result.success("Cloud Sync Successful! Synced ${depts.size} departments and ${stuList.size} students to Firebase.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
