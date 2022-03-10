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
    public static final String AVATAR_DOWNLOAD = "avatar_download";

    // -- Avatar Uploader -- //
    public static final String AVATAR_EQUIP = "avatar_equip";
    public static final String AVATAR_DELETE = "avatar_delete";

    // -- Subscriptions -- //
    public static final String SUBSCRIBE = "subscribe";

    // -- Users -- //
    public static final String USER_AVATAR_LIST = "user_avatar_list";

    public static final String USER_AVATAR_UPDATED = "user_avatar_updated";

    // -- Pings -- //
    public static final String SEND_PING = "send_ping";
    public static final String RECEIVE_PING = "receive_ping";
}
