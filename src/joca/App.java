package joca;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.spec.KeySpec;
import java.util.Random;

public class App implements ActionListener {
    private byte[] content;
    private JTextArea area;
    private JButton lock_unlock;
    private int mod;
    private JFrame f;
    private SecretKey secretKey;
    private Cipher cipher ;
    private String salt="I hate black people";
    byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    IvParameterSpec ivspec = new IvParameterSpec(iv);
    private File Page=new File("Encrypted_page.ER");


    public App(){

        f=new JFrame();
        f.setIconImage(loadimg("/logo.png").getImage());

        f.setSize(600,400);
        f.setTitle("Encryptor");
        lock_unlock=new JButton();
        area=new JTextArea();
        mod=1;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }catch (Exception eedf){}

        lock_unlock.addActionListener(this);
        f.add(new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
        f.add(lock_unlock,BorderLayout.SOUTH);
        Start();
        actionPerformed(null);



        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                switch (mod){
                    case 1:{
                        SaveData();
                    }break;
                    case 2:{
                        content=encrypt(area.getText().getBytes());
                        SaveData();
                    }
                }


            }
        });
    }

    private void SaveData(){
        try{
        FileOutputStream ofs=new FileOutputStream(Page);
        ofs.write(content);
        ofs.flush();
        ofs.close();
        }catch (Exception ee){}
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (mod){
            case 1:{
                //when content is encryted and we want to decrypt it
                byte[] b=decrypt(content);
                area.setText(new String(b));
                area.setEditable(true);
                lock_unlock.setIcon(loadimg("/unlock.png"));
                mod=2;
            }break;

            case 2:{
                //when content is decrypted and we want to encrypt it
                content=encrypt(area.getText().getBytes());
                area.setText(new String(content));
                area.setEditable(false);
                lock_unlock.setIcon(loadimg("/lock.png"));
                SaveData();

                mod=1;
            }break;

        }

    }

    private ImageIcon loadimg(String path){
       return new ImageIcon(Main.class.getResource(path));
    }

    private void generateKey(String key){
        try{
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            secretKey= new SecretKeySpec(tmp.getEncoded(), "AES");
        }catch (Exception  eee){}
    }

    protected byte[] encrypt(byte[] data){
        byte[] b=null;
        try{
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivspec);
            b=cipher.doFinal(data);
        }catch (Exception ee){System.out.println("problem\n"+ee);}

        return b;
    }

    private void Start(){
        JFileChooser c=new JFileChooser();
        c.setCurrentDirectory(Page.getAbsoluteFile().getParentFile());
        int code=c.showOpenDialog(null);

        if(code==1){
            System.exit(69);
        }

        Page=c.getSelectedFile();

        String key=JOptionPane.showInputDialog("write key that will be used for encryption and decryption");
        if(key==null){System.exit(69);}

        generateKey(key);

        boolean incriptable=true;
        try {
            FileInputStream fis = new FileInputStream(Page);
            byte[] b=fis.readAllBytes();
            fis.close();
            //if we open file for the first time
            if(b==null || b.length==0){
                content="".getBytes();
                System.out.println(b+"  "+b.length);
            }else {
                if(decrypt(b)==null) {
                    decrypt(b);
                    incriptable=false;
                }else{
                    content=b;
                }
            }
        }catch (Exception ee){}
        if(!incriptable){
            JOptionPane.showMessageDialog(null,"Ether bad key or you picked wrong file");
            Start();
        }
    }

    protected byte[] decrypt(byte[] data){
        byte[] b=null;
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey,ivspec);
            b=cipher.doFinal(data);
        }catch (Exception ee){}

        return b;
    }


}
