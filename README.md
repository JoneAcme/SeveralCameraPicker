# SeveralCameraPicker
自定义图片选择器，多张连拍

```
 SeveralImagePicker.setCompleteListener(object : PickerCompleteInterface {
            override fun onComlete(pathList: ArrayList<String>) {
                textView.text = pathList.toString()
//                Toast.makeText(this@MainActivity, "select image success${pathList.toString()}", Toast.LENGTH_SHORT).show()
            }
        }).start(this)
 ```

### SeveralImagePicker 中可设置接口：
功能 | 设置方法
---- | ---
自定义Toast  | setDefaultToast(mToast: ToastInterFace)
loadingDialog  |  setDefaultLoadingDialog(mLoadingDialog: LoadingDialogInterface)
图片加载  |  setDefaultImageLoader(mImageLoader: ImageLoaderInterface)
图片压缩  |  setDefaultCompress(mCompress: CompressInterface)
完成后的回调  |  setCompleteListener(mCompleteListener: PickerCompleteInterface)
选择照片的参数  |  setOptions( pickerOption: PickerOption) 

 >  以上接口如无要求可不设置，提供默认实现。 
 >  图片加载默认实现使用Glide
 >  压缩使用的Bitmap.compress



PickerOption   图片选择的参数：
```
//是否显示拍照按钮
    var enableCamera = true

    //最大选择张数，默认为9
    var maxPickNumber = 9
    //最小选择张数，默认为0，表示不限制
    var minPickNumber = 0
    //图片选择界面每行图片个数
    var spanCount = 4

   //目前图片宽高固定为为屏幕宽度/spanCount    暂不支持修改
    //选择列表图片宽度
    var thumbnailWidth = 100
    //选择列表图片高度     
    var thumbnailHeight = 100
    
    //选择列表点击动画效果
    var enableAnimation = true
    //是否显示gif图片
    var enableGif: Boolean = false
    //是否开启点击预览
    var enablePreview = true

    //是否开启点击声音
    var enableClickSound = true
    //预览图片时，是否增强左右滑动图片体验
    var previewEggs = true

    //是否开启压缩
    var enableCompress: Boolean = true
    //图片压缩阈值（多少kb以下的图片不进行压缩，默认1024kb）
    var compressPictureFilterSize = 1024


    //拍照保存地址
    var savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
    //压缩保存地址
    var compresssPath = ""
    //压缩质量   默认中等，50
    var compressQuality = QUALITY.MEDIA_QUALITY_MEDIUM

    //是否开启数字显示模式
    var enableNumPick:Boolean = false
```

