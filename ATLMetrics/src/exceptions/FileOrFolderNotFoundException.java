package exceptions;

public class FileOrFolderNotFoundException extends Exception {

	public FileOrFolderNotFoundException(String fileOrFolder, String name) {
		super(fileOrFolder+": ["+name+"] does not exist");
	}
}
