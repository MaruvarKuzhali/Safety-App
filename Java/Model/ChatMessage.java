package com.example.safety.Model;

public class ChatMessage {
    private boolean isImage, isMine;
    private String content;

    public ChatMessage(String message, boolean mine, boolean image){
        content = message;
        isMine = mine;
        isImage = image;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public boolean isImage() {
        return isImage;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean mine) {
        isMine = mine;
    }

    public void setIsImage(boolean image) {
        isImage = image;
    }
}
