package com.thimda.plugin.common;

public class ddd {

}

/**
 * checkout文件
 */
 public static void checkOutFile(String path){
		IPath ph = new Path(path);
		IFile ifile =  ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(ph);
		File filea = new File(path);
		//如果文件不可写，checkout,若果未连cc，变为可写
		IWorkbenchPart part = null;
		Shell shell = null;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null)
			part = page.getActivePart();
		if(part != null)
			shell = part.getSite().getShell();
		IStatus statu = ResourcesPlugin.getWorkspace().validateEdit(new IFile[]{ifile}, shell);
			if(!filea.canWrite() && !statu.isOK()){
			boolean isWritable = MessageDialog.openConfirm(null, "提示", "是否将文件变为可写?");
			if(isWritable){
				try {
					CommonTool.silentSetWriterable(path);
				} catch (Exception e) {
					CommonPlugin.getPlugin().logError(e.getMessage());
					MessageDialog.openInformation(null, "提示", e.getMessage());
				}
			}
		}
	 }
 
	/**
	 * 将文件变为可写
	 * @param filename
	 * @throws CoreException
	 */
	 public static void silentSetWriterable(String filename) throws CoreException {
     IFileInfo fileinfo = new FileInfo(filename);
     fileinfo.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
     IFileSystem fs = EFS.getLocalFileSystem();
     IFileStore store = fs.fromLocalFile(new File(filename));
     store.putInfo(fileinfo, EFS.SET_ATTRIBUTES, null);
	 }
	