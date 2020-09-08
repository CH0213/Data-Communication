import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class ChatFileDlg extends JFrame implements BaseLayer {

	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private static LayerManager m_LayerMgr = new LayerManager();

	private JTextField ChattingWrite;

	Container contentPane;

	JTextArea ChattingArea;
	JTextArea srcMacAddress;
	JTextArea dstMacAddress;
	JTextArea filedst;

	JLabel lblsrc;
	JLabel lbldst;

	JButton Setting_Button;
	JButton Chat_send_Button;
	JButton Choose_file_Button;
	JButton Send_file_Button;
	JProgressBar progressBar;
	
	static JComboBox<String> NICComboBox;

	int adapterNumber = 0;
	
	String Text;
	File file;
	
	
	public static void main(String[] args) {

		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ChatAppLayer("ChatApp"));
		m_LayerMgr.AddLayer(new FileAppLayer("FileApp"));
		m_LayerMgr.AddLayer(new ChatFileDlg("GUI"));

		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ChatApp ( *GUI ) *FileApp ( *GUI ) ) )");

	}

	public ChatFileDlg(String pName) {
		pLayerName = pName;

		setTitle("Chat & File Transfer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, 644, 425);
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel chattingPanel = new JPanel();// chatting panel
		chattingPanel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "chatting",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		chattingPanel.setBounds(10, 5, 360, 276);
		contentPane.add(chattingPanel);
		chattingPanel.setLayout(null);

		JPanel chattingEditorPanel = new JPanel();// chatting write panel
		chattingEditorPanel.setBounds(10, 15, 340, 210);
		chattingPanel.add(chattingEditorPanel);
		chattingEditorPanel.setLayout(null);

		ChattingArea = new JTextArea();
		ChattingArea.setEditable(false);
		ChattingArea.setBounds(0, 0, 340, 210);
		chattingEditorPanel.add(ChattingArea);// chatting edit

		JPanel chattingInputPanel = new JPanel();// chatting write panel
		chattingInputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		chattingInputPanel.setBounds(10, 230, 250, 20);
		chattingPanel.add(chattingInputPanel);
		chattingInputPanel.setLayout(null);

		ChattingWrite = new JTextField();
		ChattingWrite.setBounds(2, 2, 250, 20);// 249
		chattingInputPanel.add(ChattingWrite);
		ChattingWrite.setColumns(10);// writing area

		JPanel settingPanel = new JPanel();
		settingPanel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "setting",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		settingPanel.setBounds(380, 5, 236, 371);
		contentPane.add(settingPanel);
		settingPanel.setLayout(null);

		JPanel sourceAddressPanel = new JPanel();
		sourceAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		sourceAddressPanel.setBounds(10, 96, 170, 20);
		settingPanel.add(sourceAddressPanel);
		sourceAddressPanel.setLayout(null);

		lblsrc = new JLabel("Source Mac Address");
		lblsrc.setBounds(10, 75, 170, 20);
		settingPanel.add(lblsrc);

		srcMacAddress = new JTextArea();
		srcMacAddress.setBounds(2, 2, 170, 20);
		sourceAddressPanel.add(srcMacAddress);// src address

		JPanel destinationAddressPanel = new JPanel();
		destinationAddressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED,
				null, null, null, null));
		destinationAddressPanel.setBounds(10, 212, 170, 20);
		settingPanel.add(destinationAddressPanel);
		destinationAddressPanel.setLayout(null);

		lbldst = new JLabel("Destination Mac Address");
		lbldst.setBounds(10, 187, 190, 20);
		settingPanel.add(lbldst);

		dstMacAddress = new JTextArea();
		dstMacAddress.setBounds(2, 2, 170, 20);
		destinationAddressPanel.add(dstMacAddress);// dst address

		JLabel NICLabel = new JLabel("NIC 선택");
		NICLabel.setBounds(10, 20, 170, 20);
		settingPanel.add(NICLabel);

		NICComboBox = new JComboBox();
		NICComboBox.setBounds(10, 49, 170, 20);
		settingPanel.add(NICComboBox);

		for (int i = 0; ((NILayer) m_LayerMgr.GetLayer("NI")).getAdapterList()
				.size() > i; i++) {
			NICComboBox.addItem(((NILayer) m_LayerMgr.GetLayer("NI"))
					.GetAdapterObject(i).getDescription());
		}

		NICComboBox.addActionListener(new ActionListener() { // 이벤트리스너

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						adapterNumber = NICComboBox.getSelectedIndex();
						try {
							srcMacAddress.setText("");
							srcMacAddress
									.append(get_MacAddress(((NILayer) m_LayerMgr
											.GetLayer("NI")).GetAdapterObject(
											adapterNumber).getHardwareAddress()));

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

		try {// 
			srcMacAddress.append(get_MacAddress(((NILayer) m_LayerMgr
					.GetLayer("NI")).GetAdapterObject(adapterNumber)
					.getHardwareAddress()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		;

		Setting_Button = new JButton("Setting");// setting
		Setting_Button.setBounds(80, 270, 100, 20);
		Setting_Button.addActionListener(new setAddressListener());
		settingPanel.add(Setting_Button);// setting

		Chat_send_Button = new JButton("Send");
		Chat_send_Button.setBounds(270, 230, 80, 20);
		Chat_send_Button.addActionListener(new setAddressListener());
		chattingPanel.add(Chat_send_Button);// chatting send button

		JPanel filePanel = new JPanel();

		filePanel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "File Transfer",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		filePanel.setBounds(10, 285, 360, 90);
		contentPane.add(filePanel);
		filePanel.setLayout(null);

		JPanel filePanel_2 = new JPanel();// chatting write panel
		filePanel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		filePanel_2.setBounds(10, 20, 250, 20);
		filePanel.add(filePanel_2);
		filePanel_2.setLayout(null);

		filedst = new JTextArea();// �엯�젰 諛쏅뒗 �쐞移�
		filedst.setEditable(false);
		filedst.setBounds(2, 2, 250, 20);
		filePanel_2.add(filedst);// chatting edit

		Choose_file_Button = new JButton("Choose");
		Choose_file_Button.setBounds(270, 20, 80, 20);
		Choose_file_Button.addActionListener(new setAddressListener());
		Choose_file_Button.setEnabled(false);
		filePanel.add(Choose_file_Button);// chatting send button

		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setBounds(10, 50, 250, 20);
		this.progressBar.setStringPainted(true);
		filePanel.add(this.progressBar);

		Send_file_Button = new JButton("Send");
		Send_file_Button.setBounds(270, 50, 80, 20);
		Send_file_Button.addActionListener(new setAddressListener());
		Send_file_Button.setEnabled(false);
		filePanel.add(Send_file_Button);

		setVisible(true);

	}
	
	class setAddressListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			if (e.getSource() == Setting_Button) {

				if (Setting_Button.getText() == "Reset") {
					srcMacAddress.setText("");
					dstMacAddress.setText("");
					Setting_Button.setText("Setting");
					srcMacAddress.setEnabled(true);
					dstMacAddress.setEnabled(true);
				} else {
					byte[] srcAddress = new byte[6];
					byte[] dstAddress = new byte[6];

					String src = srcMacAddress.getText();
					String dst = dstMacAddress.getText();

					String[] byte_src = src.split("-");
					for (int i = 0; i < 6; i++) {
						srcAddress[i] = (byte) Integer
								.parseInt(byte_src[i], 16);
					}

					String[] byte_dst = dst.split("-");
					for (int i = 0; i < 6; i++) {
						dstAddress[i] = (byte) Integer
								.parseInt(byte_dst[i], 16);
					}

					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"))
							.SetEnetSrcAddress(srcAddress);
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"))
							.SetEnetDstAddress(dstAddress);

					((NILayer) m_LayerMgr.GetLayer("NI"))
							.SetAdapterNumber(adapterNumber);

					Setting_Button.setText("Reset");
					dstMacAddress.setEnabled(false);
					srcMacAddress.setEnabled(false);
					Choose_file_Button.setEnabled(true);
				}
				
			}
			if (e.getSource() == Chat_send_Button) {
				if (Setting_Button.getText() == "Reset") {
					String input = ChattingWrite.getText();
					ChattingArea.append("[SEND] : " + input + "\n");
					byte[] bytes = input.getBytes();
					m_LayerMgr.GetLayer("ChatApp").Send(bytes, bytes.length);
				} else {
					JOptionPane.showMessageDialog(null, "주소 설정 오류");
				}
			}
			if (e.getSource() == Choose_file_Button) {
				int chooseFile = fileChooser.showOpenDialog(null);
				if (chooseFile == fileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					filedst.setText(file.getPath());
				}
				Send_file_Button.setEnabled(true);

			}
			if (e.getSource() == Send_file_Button) {
				Send_File File_Thread = new Send_File((FileAppLayer)m_LayerMgr.GetLayer("FileApp"));
				Thread send_file = new Thread(File_Thread);
				send_file.start();
			}
		}
	}

	public String get_MacAddress(byte[] byte_MacAddress) {
		String MacAddress = "";
		for (int i = 0; i < 6; i++) {
			MacAddress += String.format("%02X%s", byte_MacAddress[i],
					(i < MacAddress.length() - 1) ? "" : "");
			if (i != 5) {
				MacAddress += "-";
			}
		}
		System.out.println("현재 선택된 주소 : " + MacAddress);
		return MacAddress;
	}

	
	class Send_File implements Runnable{
		private FileAppLayer FileLayer;
		public Send_File(FileAppLayer FileLayer){
			this.FileLayer = FileLayer;
		}
		@Override
		public void run() {
			FileLayer.setAndStartSendFile();
		}
		
	}
	
	
	public File getFile() {
		// TODO Auto-generated method stub
		return file;
	}
	
	public boolean Receive(byte[] input) {
		if (input != null) {
			byte[] data = input;
			Text = new String(data);
			ChattingArea.append("[RECV] : " + Text + "\n");
			return false;
		}
		return false;
	}


	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer.add(nUnderLayerCount++, pUnderLayer);
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {	//not in use
		// TODO Auto-generated method stub
		// if (p_UnderLayer == null)
		return null;
		// return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}
}
