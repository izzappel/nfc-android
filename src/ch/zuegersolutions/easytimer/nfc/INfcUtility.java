package ch.zuegersolutions.easytimer.nfc;

import java.io.IOException;

import ch.zuegersolutions.easytimer.model.NfcTag;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.Tag;

public interface INfcUtility {
	NfcTag readNfcTag(Intent intent);
	void writeNfcTag(Tag tag, String data) throws IOException, FormatException;
	String getNfcTagId(Tag tag);
}
