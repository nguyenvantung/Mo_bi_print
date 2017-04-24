package com.plustech.print.fileexplorer.util;

public interface OperationCallback<T> {

	T onSuccess();
	void onFailure(Throwable e);
}
