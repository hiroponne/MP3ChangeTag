package mp3.change.tag;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v2_4;

public class Model extends DefaultTableModel {
	private static final ColumunContext[] columnArray = {
			new ColumunContext("Name", String.class, true),
			new ColumunContext("Track", String.class, true),
			new ColumunContext("Song Title", String.class, true),
			new ColumunContext("Alubum Artist", String.class, true),
			new ColumunContext("Alubum Title", String.class, true),
			new ColumunContext("Full Path", String.class, true) };
	private int number = 0;
	private ArrayList<FileData> fileList = new ArrayList<FileData>();

	public void add(FileData fileData) {
		try {
			Object[] obj = { fileData.getName(),
					fileData.getTag().getTrackNumberOnAlbum(),
					fileData.getTag().getSongTitle(),
					fileData.getTag().getAlbumTitle(),
					fileData.getTag().getLeadArtist(),
					fileData.getAbsolutePath() };
			super.addRow(obj);
			number++;
			this.fileList.add(fileData);
		} catch (NullPointerException e) {
			/** MP3以外のファイルをドロップした時の対応も追加！ */
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return columnArray[col].isEditable;
	}

	@Override
	public Class<?> getColumnClass(int modelIndex) {
		return columnArray[modelIndex].columnClass;
	}

	@Override
	public int getColumnCount() {
		return columnArray.length;
	}

	@Override
	public String getColumnName(int modelIndex) {
		return columnArray[modelIndex].columnName;
	}

	public int getNumber() {
		return this.number;
	}

	public ArrayList<FileData> getFileList() {
		return this.fileList;
	}

	public void clear() {
		while (!fileList.isEmpty()) {
			removeRow(0);
			fileList.remove(0);
		}
	}

	private static class ColumunContext {
		public final String columnName;
		public final Class columnClass;
		public final boolean isEditable;

		public ColumunContext(String columnName, Class columnClass,
				boolean isEditable) {
			this.columnName = columnName;
			this.columnClass = columnClass;
			this.isEditable = isEditable;
		}
	}
}

class FileData {
	private String name, absolutePath, parentPath;
	MP3File mp3file;
	AbstractID3v2 tag;

	public FileData(String name, String absolutePath, String parentPath) {
		this.name = name;
		this.absolutePath = absolutePath;
		this.parentPath = parentPath;
		try {
			this.mp3file = new MP3File(absolutePath);
			this.tag = mp3file.getID3v2Tag();
		} catch (IOException e) {
			/** ここでなんかポップアップ出して「MP3じゃないよ！」的なものを出す(?) */
			/** MP3以外のファイルをドロップした時の対応も追加！ */
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public MP3File getMp3file() {
		return mp3file;
	}

	public void setMp3file(MP3File mp3file) {
		this.mp3file = mp3file;
	}

	public AbstractID3v2 getTag() {
		return tag;
	}

	public void setTag(AbstractID3v2 tag) {
		this.tag = tag;
	}

}
