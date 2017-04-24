package com.plustech.print.drivers.discovery;

import android.os.Parcel;
import android.os.Parcelable;

public class Printer implements Parcelable {

	private String name;
	private String address;
	private String type;
	private String rp;
	private String documentFormat;
	private String state;
	private String defaultPrinter;
	private String _id;

	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Printer(){
		
	}

	/**
	 * 
	 * @param name
	 * @param address
	 * @param type
	 * @param rp
	 * @param documentFormat
	 * @param state
	 * @param defaultPrinter
	 *            : 1 is default printer
	 */
	public Printer(String name, String address, String type, String rp,
			String documentFormat, String state, String defaultPrinter, String _id) {
		this.name = name;
		this.address = address;
		this.type = type;
		this.rp = rp;
		this.documentFormat = documentFormat;
		this.state = state;
		this.defaultPrinter = defaultPrinter;
		this._id = _id;
	}


	public String getDefaultPrinter() {
		return defaultPrinter;
	}


	public void setDefaultPrinter(String defaultPrinter) {
		this.defaultPrinter = defaultPrinter;
	}
	
	public String getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(String documentFormat) {
		this.documentFormat = documentFormat;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRp() {
		return rp;
	}

	public void setRp(String rp) {
		this.rp = rp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Printer) {
			Printer printer = (Printer) o;
			if (printer.name.equals(this.name)
					&& printer.address.equals(this.address))
				return true;
		}
		return false;
	}

	public Printer(Parcel in) {
		String[] data = new String[8];

		in.readStringArray(data);
		this.name = data[0];
		this.address = data[1];
		this.type = data[2];
		this.rp = data[3];
		this.documentFormat = data[4];
		this.state = data[5];
		this.defaultPrinter = data[6];
		this._id = data[7];
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.name, this.address,
				this.type, this.rp, this.documentFormat, this.state,
				this.defaultPrinter, this._id });
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Printer createFromParcel(Parcel in) {
			return new Printer(in);
		}

		public Printer[] newArray(int size) {
			return new Printer[size];
		}
	};

}