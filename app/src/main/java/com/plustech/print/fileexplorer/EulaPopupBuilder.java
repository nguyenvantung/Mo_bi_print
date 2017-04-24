package com.plustech.print.fileexplorer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.plustech.print.R;

public class EulaPopupBuilder {

	public static AlertDialog create(final FileExplorerMain context) {
		final TextView message = new TextView(context);
		final SpannableString s = new SpannableString(
				context.getText(R.string.eula_popup_text));
		Linkify.addLinks(s, Linkify.WEB_URLS);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());

		return new AlertDialog.Builder(context)
				.setTitle(R.string.eula_popup_title).setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(R.string.eula_accept, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						context.refresh();
						
					}
				})
				.setNegativeButton(R.string.eula_decline, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						context.finish();
					}
				})
				.setView(message).create();
	}
}