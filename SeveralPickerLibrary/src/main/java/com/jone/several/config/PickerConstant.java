package com.jone.several.config;

public final class PickerConstant {

    public static final String PICKER_RESULT = "PICKER_RESULT";
    public static final String PICKER_OPTION = "PICKER_OPTION";

    public static final int IMAGE_PROCESS_TYPE_DEFAULT = 0x000000;

    public static final int REQUEST_CODE_PICTURE_EDIT = 0x000001;
    public static final int REQUEST_CODE_CAPTURE = 0x000002;
    public static final int REQUEST_CODE_CAMERA_PERMISSIONS = 0x000100;
    public static final int REQUEST_CODE_PREVIEW = 0x000101;

    public static final int TYPE_PREIVEW_FROM_PICK = 0x000100;
    public static final int TYPE_PREIVEW_FROM_PREVIEW = 0x000101;
    public static final int TYPE_PREIVEW_FROM_CAMERA = 0x000102;

    /**
     * 拍摄照片的最大数量
     */
    public static final String KEY_TAKE_PICTURE_MAX_NUM = "KEY_TAKE_PICTURE_MAX_NUM";

    /**
     * 文件路径
     */
    public static final String KEY_FILE_PATH = "KEY_FILE_PATH";

    /**
     * 文件byte数组
     */
    public static final String KEY_FILE_BYTE = "KEY_FILE_BYTE";

    /**
     * 当前索引/位置
     */
    public static final String KEY_POSITION = "KEY_POSITION";

    /**
     * 图片方向
     */
    public static final String KEY_ORIENTATION = "KEY_ORIENTATION";

    /**
     * 图片/视频列表
     */
    public final static String KEY_ALL_LIST = "KEY_ALL_LIST";

    /**
     * 已选择的图片/视频列表
     */
    public final static String KEY_PICK_LIST = "KEY_PICK_LIST";

    public final static String EXTRA_BOTTOM_PREVIEW = "EXTRA_BOTTOM_PREVIEW";

    /**
     * 预览类型，从选择界面进入/从拍照界面进入。
     */
    public static final String KEY_PREVIEW_TYPE = "";


    public final static int FLAG_PREVIEW_UPDATE_SELECT = 2774;// 预览界面更新选中数据 标识
    public final static int CLOSE_PREVIEW_FLAG = 2770;// 关闭预览界面 标识
    public final static int FLAG_PREVIEW_COMPLETE = 2771;// 预览界面图片 标识
    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_AUDIO = 3;

    public static final int MAX_COMPRESS_SIZE = 102400;
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
}
