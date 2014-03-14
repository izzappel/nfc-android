package ch.zuegersolutions.easytimer.nfc;

import java.io.IOException;
import java.nio.charset.Charset;

import ch.zuegersolutions.easytimer.Utility;
import ch.zuegersolutions.easytimer.model.NfcTag;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;

public class NfcUtility implements INfcUtility {

	@Override
	public NfcTag readNfcTag(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if(tag != null) {
			
			NfcTag nfcTag = new NfcTag();
		
			nfcTag.setId(getNfcTagId(tag));
			
	    	Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        NdefMessage message = (NdefMessage) rawMsgs[0];
	        NdefRecord uidRecord = message.getRecords()[0];
			nfcTag.setColor(new String(uidRecord.getPayload()));
			
		    return nfcTag;
		}
	
		return null;
	}

	@Override
	public void writeNfcTag(Tag tag, String data) throws IOException, FormatException {
		Ndef ndef = Ndef.get(tag);
		if (ndef == null) {
			throw new IllegalArgumentException("Nfc Tag is not NDEF formatted.");
		}
		ndef.connect();
		if (!ndef.isWritable()) {
			throw new IllegalArgumentException("Nfc Tag is read-only.");
		}

		NdefMessage message = createMessage(data);

		int size = message.toByteArray().length;
		if (ndef.getMaxSize() < size) {
			throw new IllegalArgumentException("Tag doesn't have enough free space (Messagesize: " + size + ", MaxSize: " + ndef.getMaxSize() + ").");
		}
		
		writeNdefMessage(ndef, message);
	}
	
	@Override
	public String getNfcTagId(Tag tag) {
		return bytesToHexString(tag.getId());
	}

	public void formatNfcTag(Tag tag) throws IOException, FormatException {
		NdefFormatable format = NdefFormatable.get(tag);
		
		if (format == null) {
			throw new IllegalArgumentException("Tag couldn't be formatted to NDEF.");
		}
		try {
			format.connect();
			if(!format.isConnected()) {
				throw new IllegalArgumentException("Tag couldn't establish a connection.");
			}
			
			format.format(null);
		}  finally {
			try {
				format.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private NdefMessage createMessage(String data) {
		// record that contains our custom UID data, using custom MIME_TYPE
		byte[] payload = data.getBytes(Charset.forName("UTF-8"));

		NdefRecord appRecord = NdefRecord.createApplicationRecord(Utility.APPLICATION);
		NdefRecord colorRecord = NdefRecord.createMime(Utility.NFC_MIME_TYPE, payload);

		NdefMessage message = new NdefMessage(new NdefRecord[] { colorRecord, appRecord });
		
		return message;
	}
	
	
	private void writeNdefMessage(Ndef ndef, NdefMessage message) throws IOException, FormatException {
		try {
			ndef.writeNdefMessage(message);
		} finally {
			try {
				ndef.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	private String bytesToHexString(byte[] data) {
	    if (data == null || data.length <= 0) {
	        return null;
	    }

	    StringBuilder stringBuilder = new StringBuilder();
	    char[] buffer = new char[2];
	    for (int i = 0; i < data.length; i++) {
	        buffer[0] = Character.forDigit((data[i] >>> 4) & 0x0F, 16);  
	        buffer[1] = Character.forDigit(data[i] & 0x0F, 16);  
	        System.out.println(buffer);
	        stringBuilder.append(buffer);
	    }

	    return stringBuilder.toString();
	}
	

}
