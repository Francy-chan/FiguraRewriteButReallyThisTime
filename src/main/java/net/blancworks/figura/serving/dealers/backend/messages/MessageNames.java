package net.blancworks.figura.serving.dealers.backend.messages;

public class MessageNames {
    // -- Registry -- //
    public static final String MESSAGE_REGISTRY_INIT = "msg_reg";

    // -- ERROR -- //
    public static final String ERROR = "error";

    // -- Auth -- //
    public static final String AUTH_REQUEST = "auth_req";
    public static final String AUTH_COMPLETE = "auth_ok";


    // -- Avatar Downloader -- //
    public static final String AVATAR_DOWNLOAD_REQUEST = "avatar_download";
    public static final String AVATAR_PROVIDE = "avatar_provide";

    // -- Avatar Uploader -- //
    public static final String AVATAR_UPLOAD_REQUEST = "avatar_upload";
    public static final String AVATAR_UPLOAD_RESPONSE = "avatar_upload_response";

    // -- Subscriptions -- //
    public static final String SUBSCRIBE_TO_USERS = "subscribe";

}
