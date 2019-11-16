package com.jamesgbennett.whoknowsltesignal.file;

import android.content.Context;

import java.io.File;

public class FileHandler {

    public File createFile(Context context, String fileName){
        File file = new File(context.getFilesDir(), fileName);

        return file;
    }

    public File getFile(Context context, String fileName){
        File directory = context.getFilesDir();


        File file = new File(directory, fileName);

        return file;
    }
}
