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


 /**
     * Toast
     */
    fun setDefaultToast(mToast: ToastInterFace)
    
     /**
     * loading、 dialog
     */
    fun setDefaultLoadingDialog(mLoadingDialog: LoadingDialogInterface)
    
     /**
     * 图片加载
     */
    fun setDefaultImageLoader(mImageLoader: ImageLoaderInterface)
    
    /**
     * 图片压缩
     */
    fun setDefaultCompress(mCompress: CompressInterface)
    
     /**
     * 图片返回回调
     */
    fun setCompleteListener(mCompleteListener: PickerCompleteInterface)
    
    /**
     * 图片选择参数
     */
    fun setOptions( pickerOption: PickerOption) 
