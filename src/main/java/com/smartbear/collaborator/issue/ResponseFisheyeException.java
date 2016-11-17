package com.smartbear.collaborator.issue;

/**
 * Exception for analyzing response from Fisheye
 * Created by kpl on 17.11.2016.
 */
public class ResponseFisheyeException extends Exception {
    ResponseFisheyeException(String msg) {
        super(msg);
    }
}
