/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.jsql.util.AuthenticationUtil;
import com.jsql.util.ConfigurationUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.view.swing.HelperGui;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.text.JPopupTextField;

/**
 * A dialog for saving application settings.
 */
@SuppressWarnings("serial")
public class DialogPreference extends JDialog {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DialogPreference.class);

    /**
     * Button getting focus.
     */
    private JButton okButton;

    public int width = 350;
    public int height = 520;

    /**
     * Create Preferences panel to save jSQL settings.
     */
    public DialogPreference() {
        super(MediatorGui.frame(), "Preferences", Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(HelperGui.getIcons());

        // Action for ESCAPE key
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogPreference.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(
            escListener, 
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        okButton = new JButton("Apply");
        okButton.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, HelperGui.BLU_COLOR),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
            )
        );

        JButton cancelButton = new JButton("Close");
        cancelButton.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, HelperGui.BLU_COLOR),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
            )
        );
        cancelButton.addActionListener(escListener);

        this.getRootPane().setDefaultButton(okButton);

        this.setLayout(new BorderLayout());
        Container contentPane = this.getContentPane();

        JButton checkIPButton = new JButton("Check your IP", new ImageIcon(DialogPreference.class.getResource("/com/jsql/view/swing/resources/images/wrench.png")));
        checkIPButton.setBorder(HelperGui.BLU_ROUND_BORDER);
        checkIPButton.addActionListener(new ActionCheckIP());
        checkIPButton.setToolTipText(
            "<html><b>Verify what public IP address is used by jSQL</b><br>"
            + "Usually it's your own public IP if you don't use a proxy. If you use a proxy<br>"
            + "like TOR then your public IP is hidden and another one is used instead.</html>"
        );

        mainPanel.add(checkIPButton);
        mainPanel.add(Box.createGlue());
        mainPanel.add(okButton);
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(cancelButton);
        contentPane.add(mainPanel, BorderLayout.SOUTH);

        final JCheckBox checkboxCheckUpdateAtStartup = new JCheckBox("", ConfigurationUtil.checkUpdateAtStartup);
        checkboxCheckUpdateAtStartup.setFocusable(false);
        JButton labelUseCheckUpdateAtStartup = new JButton("Check update at startup");
        labelUseCheckUpdateAtStartup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxCheckUpdateAtStartup.setSelected(!checkboxCheckUpdateAtStartup.isSelected());
            }
        });
        
        String tooltipReportBugs = "Send unhandled exception to developer in order to fix issues.";
        final JCheckBox checkboxReportBugs = new JCheckBox("", ConfigurationUtil.reportBugs);
        checkboxReportBugs.setToolTipText(tooltipReportBugs);
        checkboxReportBugs.setFocusable(false);
        JButton labelReportBugs = new JButton("Report unhandled exception");
        labelReportBugs.setToolTipText(tooltipReportBugs);
        labelReportBugs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxReportBugs.setSelected(!checkboxReportBugs.isSelected());
            }
        });
        
        String tooltipEnableEvasion = "Use complex SQL syntaxes to bypass protection (slower).";
        final JCheckBox checkboxEnableEvasion = new JCheckBox("", ConfigurationUtil.enableEvasion);
        checkboxEnableEvasion.setToolTipText(tooltipEnableEvasion);
        checkboxEnableEvasion.setFocusable(false);
        JButton labelEnableEvasion = new JButton("Enable evasion");
        labelEnableEvasion.setToolTipText(tooltipEnableEvasion);
        labelEnableEvasion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxEnableEvasion.setSelected(!checkboxEnableEvasion.isSelected());
            }
        });
        
        String tooltipFollowRedirection = "Force redirection when the page has moved (e.g. HTTP/1.1 302 Found).";
        final JCheckBox checkboxFollowRedirection = new JCheckBox("", ConfigurationUtil.followRedirection);
        checkboxFollowRedirection.setToolTipText(tooltipFollowRedirection);
        checkboxFollowRedirection.setFocusable(false);
        JButton labelFollowRedirection = new JButton("Follow HTTP redirection");
        labelFollowRedirection.setToolTipText(tooltipFollowRedirection);
        labelFollowRedirection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxFollowRedirection.setSelected(!checkboxFollowRedirection.isSelected());
            }
        });

        LineBorder roundedLineBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "General");
        
        // Second panel hidden by default, contain proxy setting
        final JPanel settingPanel = new JPanel();
        GroupLayout settingLayout = new GroupLayout(settingPanel);
        settingPanel.setLayout(settingLayout);
        settingPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    roundedTitledBorder
                ), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            )
        );

        // Proxy label
        JLabel labelProxyAddress = new JLabel("Proxy address  ");
        JLabel labelProxyPort = new JLabel("Proxy port  ");
        JButton labelUseProxy = new JButton("Use a proxy");
        String tooltipUseProxy = "Enable proxy communication (e.g. TOR with Privoxy or Burp).";
        labelUseProxy.setToolTipText(tooltipUseProxy);

        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textProxyAddress = new JPopupTextField("e.g Tor address: 127.0.0.1", ProxyUtil.proxyAddress).getProxy();
        final JTextField textProxyPort = new JPopupTextField("e.g Tor port: 8118", ProxyUtil.proxyPort).getProxy();
        final JCheckBox checkboxUseProxy = new JCheckBox("", ProxyUtil.useProxy);
        checkboxUseProxy.setToolTipText(tooltipUseProxy);
        checkboxUseProxy.setFocusable(false);

        labelUseProxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxUseProxy.setSelected(!checkboxUseProxy.isSelected());
            }
        });
        
        // Digest label
        JLabel labelDigestAuthenticationUsername = new JLabel("Username  ");
        JLabel labelDigestAuthenticationPassword = new JLabel("Password  ");
        final JButton labelUseDigestAuthentication = new JButton("Enable Basic, Digest, NTLM");
        String tooltipUseDigestAuthentication = 
                "<html>"
                + "Enable <b>Basic</b>, <b>Digest</b>, <b>NTLM</b> authentication (e.g. WWW-Authenticate).<br>"
                + "Then define username and password for the host.<br>"
                + "<i><b>Negotiate</b> authentication is defined in URL.</i>"
                + "</html>";
        labelUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textDigestAuthenticationUsername = new JPopupTextField("Host system user", AuthenticationUtil.digestUsername).getProxy();
        final JTextField textDigestAuthenticationPassword = new JPopupTextField("Host system password", AuthenticationUtil.digestPassword).getProxy();
        final JCheckBox checkboxUseDigestAuthentication = new JCheckBox("", AuthenticationUtil.enableDigestAuthentication);
        checkboxUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        checkboxUseDigestAuthentication.setFocusable(false);
        
        // Digest label
        JLabel labelKerberosLoginConf = new JLabel("login.conf  ");
        JLabel labelKerberosKrb5Conf = new JLabel("krb5.conf  ");
        final JButton labelUseKerberos = new JButton("Enable Kerberos");
        String tooltipUseKerberos = 
            "<html>"
            + "Activate Kerberos authentication, then define path to <b>login.conf</b> and <b>krb5.conf</b>.<br>"
            + "Path to <b>.keytab</b> file is defined in login.conf ; name of <b>principal</b> must be correct.<br>"
            + "<b>Realm</b> and <b>kdc</b> are defined in krb5.conf.<br>"
            + "Finally use the <b>correct hostname</b> in URL, e.g. http://servicename.corp.test/[..]"
            + "</html>";
        labelUseKerberos.setToolTipText(tooltipUseKerberos);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textKerberosLoginConf = new JPopupTextField("Path to login.conf", AuthenticationUtil.kerberosLoginConf).getProxy();
        final JTextField textKerberosKrb5Conf = new JPopupTextField("Path to krb5.conf", AuthenticationUtil.kerberosKrb5Conf).getProxy();
        final JCheckBox checkboxUseKerberos = new JCheckBox("", AuthenticationUtil.enableKerberos);
        textKerberosLoginConf.setToolTipText(
            "<html>"
            + "Define the path to <b>login.conf</b>. Sample :<br>"
            + "&emsp;<b>entry-name</b> {<br>"
            + "&emsp;&emsp;com.sun.security.auth.module.Krb5LoginModule<br>"
            + "&emsp;&emsp;required<br>"
            + "&emsp;&emsp;useKeyTab=true<br>"
            + "&emsp;&emsp;keyTab=\"<b>/path/to/my.keytab</b>\"<br>"
            + "&emsp;&emsp;principal=\"<b>HTTP/SERVICENAME.CORP.TEST@CORP.TEST</b>\"<br>"
            + "&emsp;&emsp;debug=false;<br>"
            + "&emsp;}<br>"
            + "<i>Principal name is case sensitive ; entry-name is read automatically.</i>"
            + "</html>");
        textKerberosKrb5Conf.setToolTipText(
            "<html>"
            + "Define the path to <b>krb5.conf</b>. Sample :<br>"
            + "&emsp;[libdefaults]<br>"
            + "&emsp;&emsp;default_realm = <b>CORP.TEST</b><br>"
            + "&emsp;&emsp;udp_preference_limit = 1<br>"
            + "&emsp;[realms]<br>"
            + "&emsp;&emsp;<b>CORP.TEST</b> = {<br>"
            + "&emsp;&emsp;&emsp;kdc = <b>127.0.0.1:88</b><br>"
            + "&emsp;&emsp;}<br>"
            + "<i>Realm and kdc are case sensitives.</i>"
            + "</html>");
        checkboxUseKerberos.setToolTipText(tooltipUseKerberos);
        checkboxUseKerberos.setFocusable(false);
        
        labelUseKerberos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxUseKerberos.setSelected(!checkboxUseKerberos.isSelected());
                if (checkboxUseKerberos.isSelected()) {
                    checkboxUseDigestAuthentication.setSelected(false);
                }
            }
        });
        
        labelUseDigestAuthentication.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxUseDigestAuthentication.setSelected(!checkboxUseDigestAuthentication.isSelected());
                if (checkboxUseDigestAuthentication.isSelected()) {
                    checkboxUseKerberos.setSelected(false);
                }
            }
        });
        
        textProxyAddress.setFont(textProxyAddress.getFont().deriveFont(Font.PLAIN, textProxyAddress.getFont().getSize() + 2));
        textProxyPort.setFont(textProxyPort.getFont().deriveFont(Font.PLAIN, textProxyPort.getFont().getSize() + 2));
        textKerberosLoginConf.setFont(textKerberosLoginConf.getFont().deriveFont(Font.PLAIN, textKerberosLoginConf.getFont().getSize() + 2));
        textKerberosKrb5Conf.setFont(textKerberosKrb5Conf.getFont().deriveFont(Font.PLAIN, textKerberosKrb5Conf.getFont().getSize() + 2));
        
        textDigestAuthenticationUsername.setFont(
            textDigestAuthenticationUsername.getFont().deriveFont(
                Font.PLAIN, textDigestAuthenticationUsername.getFont().getSize() + 2
            )
        );
        textDigestAuthenticationPassword.setFont(
            textDigestAuthenticationPassword.getFont().deriveFont(
                Font.PLAIN, textDigestAuthenticationPassword.getFont().getSize() + 2
            )
        );
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ConfigurationUtil.set(
                    checkboxCheckUpdateAtStartup.isSelected(), 
                    checkboxReportBugs.isSelected(), 
                    checkboxEnableEvasion.isSelected(), 
                    checkboxFollowRedirection.isSelected()
                );
                
                ProxyUtil.set(
                    checkboxUseProxy.isSelected(), 
                    textProxyAddress.getText(), 
                    textProxyPort.getText()
                );
                
                AuthenticationUtil.set(
                    checkboxUseDigestAuthentication.isSelected(), 
                    textDigestAuthenticationUsername.getText(), 
                    textDigestAuthenticationPassword.getText(),
                    checkboxUseKerberos.isSelected(),
                    textKerberosKrb5Conf.getText(),
                    textKerberosLoginConf.getText()
                );

                LOGGER.info("Preferences saved.");
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                textProxyAddress.setText(ProxyUtil.proxyAddress);
                textProxyPort.setText(ProxyUtil.proxyPort);
                checkboxUseProxy.setSelected(ProxyUtil.useProxy);

                checkboxCheckUpdateAtStartup.setSelected(ConfigurationUtil.checkUpdateAtStartup);
                checkboxReportBugs.setSelected(ConfigurationUtil.reportBugs);
                checkboxEnableEvasion.setSelected(ConfigurationUtil.enableEvasion);
                checkboxFollowRedirection.setSelected(ConfigurationUtil.followRedirection);
            }
        });

        labelUseCheckUpdateAtStartup.setHorizontalAlignment(JButton.LEFT);
        labelUseCheckUpdateAtStartup.setBorderPainted(false);
        labelUseCheckUpdateAtStartup.setContentAreaFilled(false); 
        
        labelReportBugs.setHorizontalAlignment(JButton.LEFT);
        labelReportBugs.setBorderPainted(false);
        labelReportBugs.setContentAreaFilled(false); 
        
        labelEnableEvasion.setHorizontalAlignment(JButton.LEFT);
        labelEnableEvasion.setBorderPainted(false);
        labelEnableEvasion.setContentAreaFilled(false); 
        
        labelFollowRedirection.setHorizontalAlignment(JButton.LEFT);
        labelFollowRedirection.setBorderPainted(false);
        labelFollowRedirection.setContentAreaFilled(false); 
        
        labelUseProxy.setHorizontalAlignment(JButton.LEFT);
        labelUseProxy.setBorderPainted(false);
        labelUseProxy.setContentAreaFilled(false); 
        
        labelUseDigestAuthentication.setHorizontalAlignment(JButton.LEFT);
        labelUseDigestAuthentication.setBorderPainted(false);
        labelUseDigestAuthentication.setContentAreaFilled(false); 
        
        labelUseKerberos.setHorizontalAlignment(JButton.LEFT);
        labelUseKerberos.setBorderPainted(false);
        labelUseKerberos.setContentAreaFilled(false); 
        
        JLabel authenticationField = new JLabel("<html><b>Authentication</b></html>", JLabel.RIGHT);
        JLabel authenticationInfo = new JLabel(" / Basic, Digest, NTLM or Kerberos");
        authenticationField.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel proxyField = new JLabel("<html><b>Proxy</b></html>", JLabel.RIGHT);
        JLabel proxyInfo = new JLabel(" / Define proxy settings (e.g. TOR)");
        proxyField.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        
        JLabel preferencesField = new JLabel("<html><b>Other</b></html>", JLabel.RIGHT);
        JLabel preferencesInfo = new JLabel(" / Standard options");
        preferencesField.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        
        // Proxy settings, Horizontal column rules
        settingLayout.setHorizontalGroup(
            settingLayout.createSequentialGroup()
            .addGroup(
                settingLayout
                    .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(proxyField)
                    .addComponent(checkboxUseProxy)
                    .addComponent(labelProxyAddress)
                    .addComponent(labelProxyPort)
                    .addComponent(authenticationField)
                    .addComponent(checkboxUseDigestAuthentication)
                    .addComponent(labelDigestAuthenticationUsername)
                    .addComponent(labelDigestAuthenticationPassword)
                    .addComponent(checkboxUseKerberos)
                    .addComponent(labelKerberosLoginConf)
                    .addComponent(labelKerberosKrb5Conf)
                    .addComponent(preferencesField)
                    .addComponent(checkboxCheckUpdateAtStartup)
                    .addComponent(checkboxReportBugs)
                    .addComponent(checkboxEnableEvasion)
                    .addComponent(checkboxFollowRedirection)
            ).addGroup(
                settingLayout
                    .createParallelGroup()
                    .addComponent(proxyInfo)
                    .addComponent(labelUseProxy)
                    .addComponent(textProxyAddress)
                    .addComponent(textProxyPort)
                    .addComponent(authenticationInfo)
                    .addComponent(labelUseDigestAuthentication)
                    .addComponent(textDigestAuthenticationUsername)
                    .addComponent(textDigestAuthenticationPassword)
                    .addComponent(labelUseKerberos)
                    .addComponent(textKerberosLoginConf)
                    .addComponent(textKerberosKrb5Conf)
                    .addComponent(preferencesInfo)
                    .addComponent(labelUseCheckUpdateAtStartup)
                    .addComponent(labelReportBugs)
                    .addComponent(labelEnableEvasion)
                    .addComponent(labelFollowRedirection)
        ));

        // Proxy settings, Vertical line rules
        settingLayout.setVerticalGroup(
            settingLayout
                .createSequentialGroup()
                .addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(proxyInfo)
                        .addComponent(proxyField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxUseProxy)
                        .addComponent(labelUseProxy)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyAddress)
                        .addComponent(textProxyAddress)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyPort)
                        .addComponent(textProxyPort)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(authenticationInfo)
                        .addComponent(authenticationField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxUseDigestAuthentication)
                        .addComponent(labelUseDigestAuthentication)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDigestAuthenticationUsername)
                        .addComponent(textDigestAuthenticationUsername)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDigestAuthenticationPassword)
                        .addComponent(textDigestAuthenticationPassword)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxUseKerberos)
                        .addComponent(labelUseKerberos)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelKerberosLoginConf)
                        .addComponent(textKerberosLoginConf)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelKerberosKrb5Conf)
                        .addComponent(textKerberosKrb5Conf)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(preferencesInfo)
                        .addComponent(preferencesField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxCheckUpdateAtStartup)
                        .addComponent(labelUseCheckUpdateAtStartup)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxReportBugs)
                        .addComponent(labelReportBugs)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxEnableEvasion)
                        .addComponent(labelEnableEvasion)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxFollowRedirection)
                        .addComponent(labelFollowRedirection)
                    )
                )
        ;

        contentPane.add(settingPanel, BorderLayout.CENTER);

        this.pack();
        this.height = this.getHeight() + 5;
        this.setMinimumSize(new Dimension(this.width, this.height));
        this.getRootPane().setDefaultButton(okButton);
        cancelButton.requestFocusInWindow();
        this.setLocationRelativeTo(MediatorGui.frame());
    }
    
    public void requestButtonFocus() {
        this.okButton.requestFocusInWindow();
    }
}
