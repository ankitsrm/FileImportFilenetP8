package com.filenetp8.batchimport.service;
/**
 * @ Ankit Utkarsh
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.filenetp8.batchimport.services.exception.FBIU_Exception;
import com.filenetp8.batchimport.services.util.BatchThreadPool;
import com.filenetp8.batchimport.services.util.LoggerUtil;

import org.apache.log4j.Logger;

public class UnzipBatch {
    private Logger log = LoggerUtil.getLogger(UnzipBatch.class);
    private static final int BUFFER_SIZE = 4096;

    public File getUnzipFile (File zipFile){
        log.info(BatchThreadPool.get()+" | "+ "  Unzipping of batches is initiated.");
        log.info(BatchThreadPool.get()+" | "+ " Zip File : " +zipFile.getPath());

        boolean isProcessCompleted = false;
        ZipInputStream zipIn = null;
        ZipEntry zEntry = null;
        File batchDirec = getCreateDirec(zipFile);
        log.info(BatchThreadPool.get()+" | "+" Batch Direc Created : " + batchDirec.getPath());

        if (batchDirec.exists() && batchDirec.isDirectory() ){
            try {
                zipIn = new ZipInputStream(new FileInputStream(zipFile));
            } catch (FileNotFoundException e) {
                FBIU_Exception fbException = new FBIU_Exception(BatchThreadPool.get()+" | "+" Zip file not found.", e);
                log.error(fbException.getExceptionTrace());
              
            }

            try {
                zEntry = zipIn.getNextEntry();
            } catch (IOException e) {
                FBIU_Exception fbException = new FBIU_Exception(BatchThreadPool.get()+" | "+" Zip file dont have proper entry.", e);
                log.error(fbException.getExceptionTrace());
            }

            while (zEntry != null){
                log.info(BatchThreadPool.get()+" | "+" File Inside Zip Batch : " + zEntry.getName());
                String zipFileEntryPath = batchDirec.getPath() + File.separator + zEntry.getName();
                getExtractZipFile(zipFileEntryPath, zipIn);
                log.info(BatchThreadPool.get() + " | "+ " Extracted file  : " + zipFileEntryPath);
                try {
                    zEntry = zipIn.getNextEntry();
                } catch (IOException e) {
                    FBIU_Exception fbException = new FBIU_Exception(BatchThreadPool.get()+" | "+" Zip file dont have proper entry.", e);
                    log.error(fbException.getExceptionTrace());
                }
            }
            isProcessCompleted = true;           
        }else{
            log.error(BatchThreadPool.get()+ " | "+" Batch directory not created so breaking the process for this batch '" + zipFile.getPath() + "'.");
        }
        if(isProcessCompleted){
            return batchDirec;
        }else{
            return null;
        }
        
    }
    private File getCreateDirec (File zipFile) {
        
        String zipFileName = zipFile.getName();
        String zipFileParentPath = zipFile.getParent();
        String newBatchDirec = zipFileParentPath+ File.separator+ zipFileName;

        File batchDirec = new File(newBatchDirec);
        return batchDirec;
    }

    private void getExtractZipFile (String zipFileEntryPath, ZipInputStream zipIn){
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(zipFileEntryPath));
        }catch(FileNotFoundException e){
            FBIU_Exception fbiu_Exception = new FBIU_Exception( BatchThreadPool.get()+" | "+ " Zip file entry path not found ' " + zipFileEntryPath + "'." , e);
            log.error(fbiu_Exception.getExceptionTrace());
        }

        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0 ;
        try {
                while((read = zipIn.read(bytesIn))!= -1){
                    if (bos != null){
                        bos.write(bytesIn,0,read);
                    }else
                        break;
                }
            
        } catch (IOException e) {
            FBIU_Exception fbiu_Exception = new FBIU_Exception(BatchThreadPool.get()+ " | " + "Zip file input stream isnt able to read properly.", e);
            log.error(fbiu_Exception.getExceptionTrace());
        }finally{
            try {
                bos.close();
            } catch (IOException e) {
                FBIU_Exception fbiu_Exception = new FBIU_Exception( BatchThreadPool.get()+" | "+ " Buffer output stream failed to close ' " + zipFileEntryPath + "'." , e);
            log.error(fbiu_Exception.getExceptionTrace());
            }
        }
    }
}