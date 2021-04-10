package de.markusfisch.android.binaryeye.database

import android.app.Activity
import android.os.Environment
import de.markusfisch.android.binaryeye.io.writeExternalFile
import java.io.File
import java.io.FileInputStream

fun exportDatabase(activity: Activity, fileName: String): Boolean {
	val dbFile = File(
		Environment.getDataDirectory(),
		"//data//${activity.packageName}//databases//${Database.FILE_NAME}"
	)
	if (!dbFile.exists()) {
		return false
	}
	return writeExternalFile(
		activity,
		fileName,
		"application/vnd.sqlite3"
	) {
		FileInputStream(dbFile).copyTo(it)
	}
}
