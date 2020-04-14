package minecraftserveradmin.core.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileModel {
    public String md5;
    private String name; //文件名或目录名
    private String type; //文件类型
    private int attr; //用来识别是文件还是目录
    private long size;   //文件的大小
    private FileModel father = null;    //该文件或目录的上级目录
    private String onwer;

    public FileModel(String name, String type, int attr, long size, FileModel father, byte[] md5) {
        this.name = name;
        this.type = type;
        this.attr = attr;
        this.size = size;
        this.father = father;
        if (md5 != null){
            this.md5 = parseBytes2Hex(md5);
        }else{
            this.md5 = null;
        }

    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getOnwer() {
        return onwer;
    }

    public void setOnwer(String onwer) {
        this.onwer = onwer;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getAttr() {
        return attr;
    }
    public void setAttr(int attr) {
        this.attr = attr;
    }
    public long getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public FileModel getFather() {
        return father;
    }

    public void setFather(FileModel father) {
        this.father = father;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "subMap=" + md5 +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", attr=" + attr +
                ", size=" + size +
                ", father=" + father +
                '}';
    }

    public  String parseBytes2Hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

}
