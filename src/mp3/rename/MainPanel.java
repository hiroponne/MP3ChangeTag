package mp3.rename;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
	private final Model model = new Model();
	private final JTable table;
	private final JCheckBox[] checkBox = new JCheckBox[4];
	private final JButton renameButton;
	private final String root;

	public MainPanel() {
		super(new BorderLayout());

		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") >= 0) {
			root = "\\";
		} else {
			root = "/";
		}

		/** rename chack box */
		for (int i = 0; i < 4; i++) {
			checkBox[i] = new JCheckBox();
		}
		JPanel renamePanel = new JPanel();
		renamePanel.setLayout(new GridLayout(5, 1));
		checkBox[0].setText("Track Number");
		checkBox[1].setText("Artist");
		checkBox[2].setText("Album");
		checkBox[3].setText("Song Title");
		renameButton = new JButton("rename");
		renameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (FileData list : model.getFileList()) {
					String[] str = new String[4];
					String fileName = "";
					int trackNumber;
					int cnt = -1;
					if (checkBox[0].isSelected()) {
						str[0] = list.getTag().getTrackNumberOnAlbum();
						trackNumber = Integer.parseInt(str[0]);
						if (trackNumber < 10) {
							str[0] = "0" + trackNumber;
						}
						cnt++;
					}
					if (checkBox[1].isSelected()) {
						str[1] = list.getTag().getLeadArtist();
						cnt++;
					}
					if (checkBox[2].isSelected()) {
						str[2] = list.getTag().getAlbumTitle();
						cnt++;
					}
					if (checkBox[3].isSelected()) {
						str[3] = list.getTag().getSongTitle();
						cnt++;
					}
					for (int i = 0; i < 4; i++) {
						if (str[i] != null) {
							fileName += str[i];
							if (cnt > 0) {
								fileName += " - ";
								cnt--;
							}
						}
					}
					if (cnt >= 0) {
						new File(list.getAbsolutePath()).renameTo(new File(list
								.getParentPath() + root + fileName + ".mp3"));
					}
				}
				model.clear();

			}
		});
		for (int i = 0; i < 4; i++) {
			renamePanel.add(checkBox[i]);
		}
		renamePanel.add(renameButton);

		/** file table */
		table = new JTable(model);

		/* ここあとでgoogle先生 */
		DropTargetListener dropTargetListener = new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						dtde.acceptDrop(DnDConstants.ACTION_COPY);
						Transferable transferable = dtde.getTransferable();
						java.util.List list = (java.util.List) transferable
								.getTransferData(DataFlavor.javaFileListFlavor);
						for (Object obj : list) {
							if (obj instanceof File) {
								File file = (File) obj;
								FileData fileData = new FileData(
										file.getName(), file.getAbsolutePath(),
										file.getParent());
								model.add(fileData);
							}
						}
						dtde.dropComplete(true);
						return;
					}
				} catch (UnsupportedFlavorException ufe) {
					ufe.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				dtde.rejectDrop();
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde) {
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
					return;
				}
				dtde.rejectDrag();
			}
		};

		new DropTarget(table, DnDConstants.ACTION_COPY, dropTargetListener,
				true);

		TableColumn[] tableColumn = new TableColumn[6];
		for (int i = 0; i < model.getColumnCount(); i++) {
			tableColumn[i] = table.getColumnModel().getColumn(i);
			tableColumn[i].setResizable(true);
		}
		tableColumn[0].setMinWidth(160);
		tableColumn[1].setMaxWidth(40);

		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		table.setComponentPopupMenu(new TablePopupMenu());
		add(renamePanel, BorderLayout.NORTH);
		add(new JScrollPane(table));
		setPreferredSize(new Dimension(800, 600));
	}

	class DeleteAction extends AbstractAction {
		public DeleteAction(String label, Icon icon) {
			super(label, icon);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			deleteActionPerformed(event);
		}
	}

	public void deleteActionPerformed(ActionEvent event) {
		int[] selection = table.getSelectedRows();
		if (selection == null || selection.length <= 0)
			return;
		for (int i = selection.length - 1; i >= 0; i--) {
			model.removeRow(table.convertColumnIndexToModel(selection[i]));
		}
	}

	private class TablePopupMenu extends JPopupMenu {
		private final Action deleteAction = new DeleteAction("delete", null);

		public TablePopupMenu() {
			super();
			add(deleteAction);
		}

		@Override
		public void show(Component component, int x, int y) {
			int[] rows = table.getSelectedRows();
			deleteAction.setEnabled(rows != null && rows.length > 0);
			super.show(component, x, y);

		}
	}

	public static void Graphics() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("JFrame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new MainPanel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}