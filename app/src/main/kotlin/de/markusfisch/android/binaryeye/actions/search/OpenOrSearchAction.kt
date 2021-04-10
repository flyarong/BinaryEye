package de.markusfisch.android.binaryeye.actions.search

import android.content.Context
import android.content.Intent
import de.markusfisch.android.binaryeye.R
import de.markusfisch.android.binaryeye.actions.IAction
import de.markusfisch.android.binaryeye.app.alertDialog
import de.markusfisch.android.binaryeye.app.parseAndNormalizeUri
import de.markusfisch.android.binaryeye.app.prefs
import de.markusfisch.android.binaryeye.content.execShareIntent
import de.markusfisch.android.binaryeye.widget.toast
import java.net.URLEncoder

object OpenOrSearchAction : IAction {
	override val iconResId: Int = R.drawable.ic_action_search
	override val titleResId: Int = R.string.search_web

	override fun canExecuteOn(data: ByteArray): Boolean = false

	override suspend fun execute(context: Context, data: ByteArray) {
		val intent = openUri(context, String(data)) ?: return
		context.execShareIntent(intent)
	}

	private suspend fun openUri(
		context: Context,
		data: String,
		search: Boolean = true
	): Intent? {
		val uri = parseAndNormalizeUri(data)
		val intent = Intent(Intent.ACTION_VIEW, uri)
		return when {
			// It's okay to use `resolveActivity()` at API level 30+ here
			// because ACTION_VIEW is defined in `<queries>` in the Manifest.
			intent.resolveActivity(context.packageManager) != null -> intent
			search -> getSearchIntent(context, data)
			else -> {
				context.toast(R.string.cannot_resolve_action)
				null
			}
		}
	}

	private suspend fun getSearchIntent(context: Context, query: String): Intent? {
		val names = context.resources.getStringArray(
			R.array.search_engines_names
		).toMutableList()
		val urls = context.resources.getStringArray(
			R.array.search_engines_values
		).toMutableList()
		if (prefs.openWithUrl.isNotEmpty()) {
			names.add(prefs.openWithUrl)
			urls.add(prefs.openWithUrl)
		}
		val queryUri = alertDialog<String>(context) { resume ->
			setTitle(R.string.pick_search_engine)
			setItems(names.toTypedArray()) { _, which ->
				resume(urls[which] + URLEncoder.encode(query, "utf-8"))
			}
		} ?: return null
		return openUri(context, queryUri, false)
	}
}
