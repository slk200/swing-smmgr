package org.tizzer.smmgr.system.view;

import com.alee.extended.image.DisplayType;
import com.alee.extended.image.WebImage;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import com.alee.utils.TimeUtils;
import org.tizzer.smmgr.system.constant.ColorManager;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.handler.HttpHandler;
import org.tizzer.smmgr.system.model.request.LoginRequestDto;
import org.tizzer.smmgr.system.model.response.LoginResponseDto;
import org.tizzer.smmgr.system.utils.NPatchUtil;
import org.tizzer.smmgr.system.utils.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginBoundary extends WebPanel {

    private WebImage image1;
    private WebImage image2;
    private WebImage image3;
    private ComponentTransition animationComponent;
    private WebTextField staffNoField;
    private WebPasswordField passwordField;
    private WebTextField securityField;
    private WebButton securityButton;
    private WebButton loginButton;

    public LoginBoundary() {
        image1 = createPosterImage(IconManager.POSTER1);
        image2 = createPosterImage(IconManager.POSTER2);
        image3 = createPosterImage(IconManager.POSTER3);
        animationComponent = createAnimationComponent();
        staffNoField = createBootstrapField(15, "请输入员工号", new WebImage(IconManager.ACCOUNT));
        passwordField = createBootstrapPasswordField(new WebImage(IconManager.PASSWORD));
        securityField = createBootstrapField(8, "请输入验证码", new WebImage(IconManager.SECURITY));
        securityButton = createBootstrapButton(getSecurityCode());
        loginButton = createBootstrapButton("登录");

        this.setMargin(150);
        this.setLayout(new TableLayout(new double[][]{
                {TableLayout.FILL, TableLayout.PREFERRED},
                {TableLayout.FILL}
        }));
        this.add(animationComponent, "0,0");
        this.add(createLoginPanel(), "1,0");
        this.initListener();
        this.startAnimation();
    }

    public void initListener() {
        securityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                securityButton.setText(getSecurityCode());
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String staffNo = staffNoField.getText();
                if (staffNo.equals("")) {
                    SwingUtil.showTip(staffNoField, "账号不能为空");
                    return;
                }
                String password = passwordField.getText();
                if (password.equals("")) {
                    SwingUtil.showTip(passwordField, "密码不能为空");
                    return;
                }
                String securityCode = securityButton.getText().replaceAll(" ", "");
                if (!securityField.getText().equals(securityCode.replaceAll(" ", ""))) {
                    SwingUtil.showTip(securityField, "验证码不正确");
                    return;
                }
                verify(staffNo, password);
            }
        });
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (animationComponent.getContent() == image1) {
                    animationComponent.performTransition(image2);
                } else if (animationComponent.getContent() == image2) {
                    animationComponent.performTransition(image3);
                } else {
                    animationComponent.performTransition(image1);
                }
                startAnimation();
            }
        }, 3000);
    }

    /**
     * 设置全局默认按钮
     */
    public void setDefaultButton() {
        getRootPane().setDefaultButton(loginButton);
    }

    public void setClosingOperation(WebFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * 生成安全码
     *
     * @return
     */
    private String getSecurityCode() {
        String code = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            code += random.nextInt(10) + " ";
        }
        code += random.nextInt(10);
        return code;
    }

    /**
     * 执行服务器验证
     *
     * @param staffNo
     * @param password
     */
    private void verify(String staffNo, String password) {
        try {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setStaffNo(staffNo);
            loginRequestDto.setPassword(password);
            LoginResponseDto loginResponseDto = HttpHandler.post("/login", loginRequestDto.toString(), LoginResponseDto.class);
            if (loginResponseDto.getCode() == ResultCode.ERROR) {
                securityButton.setText(getSecurityCode());
                SwingUtil.showTip(loginButton, loginResponseDto.getMessage());
                return;
            }
            if (loginResponseDto.getCode() == ResultCode.OK) {
                enter(loginResponseDto.getAdmin());
                updateRuntimeParam(staffNo, loginResponseDto.getStoreId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户权限进入管理系统
     *
     * @param isAdmin
     */
    private void enter(boolean isAdmin) {
        Container parent = getParent();
        parent.removeAll();
        parent.add(isAdmin ? new LaunchManageMode() : new LaunchStandardMode());
        parent.validate();
        parent.repaint();
    }

    /**
     * 更新运行时参数
     *
     * @param staffNo
     * @param storeId
     */
    private void updateRuntimeParam(String staffNo, Integer storeId) {
        RuntimeConstants.staffNo = staffNo;
        RuntimeConstants.storeId = storeId;
        RuntimeConstants.loginAt = TimeUtils.formatCurrentDate("yy-MM-dd HH:mm:ss");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradientPaint = new GradientPaint(0, 0, Color.WHITE, getWidth() * 3 / 4, getHeight() * 3 / 4, ColorManager._28_102_220, false);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private WebImage createPosterImage(ImageIcon icon) {
        WebImage webImage = new WebImage(icon);
        webImage.setDisplayType(DisplayType.fitComponent);
        return webImage;
    }

    private ComponentTransition createAnimationComponent() {
        ComponentTransition componentTransition = new ComponentTransition();
        componentTransition.setOpaque(false);
        componentTransition.setContent(image1);
        componentTransition.addTransitionEffect(createFadeTransitionEffect());
        return componentTransition;
    }

    private FadeTransitionEffect createFadeTransitionEffect() {
        FadeTransitionEffect fadeTransitionEffect = new FadeTransitionEffect();
        fadeTransitionEffect.setMinimumSpeed(0.03f);
        fadeTransitionEffect.setSpeed(0.1f);
        return fadeTransitionEffect;
    }

    private WebTextField createBootstrapField(int column, String inputPrompt, JComponent leadingComponent) {
        WebTextField webTextField = new WebTextField(column);
        webTextField.setFieldMargin(0, 6, 0, 0);
        webTextField.setInputPrompt(inputPrompt);
        webTextField.setLeadingComponent(leadingComponent);
        webTextField.setPainter(NPatchUtil.getNinePatchPainter("androidstylefield.xml"));
        return webTextField;
    }

    private WebPasswordField createBootstrapPasswordField(JComponent leadingComponent) {
        WebPasswordField webPasswordField = new WebPasswordField(15);
        webPasswordField.setFieldMargin(0, 6, 0, 0);
        webPasswordField.setInputPrompt("请输入密码");
        webPasswordField.setLeadingComponent(leadingComponent);
        webPasswordField.setPainter(NPatchUtil.getNinePatchPainter("androidstylefield.xml"));
        return webPasswordField;
    }

    private WebButton createBootstrapButton(String text) {
        WebButton webButton = new WebButton(text);
        webButton.setBoldFont(true);
        webButton.setForeground(Color.WHITE);
        webButton.setSelectedForeground(Color.WHITE);
        webButton.setCursor(Cursor.getDefaultCursor());
        webButton.setPainter(NPatchUtil.getNinePatchPainter("default.xml"));
        return webButton;
    }

    private WebLabel createTitleLabel() {
        WebLabel webLabel = new WebLabel();
        webLabel.setHorizontalAlignment(WebLabel.CENTER);
        webLabel.setText("超市管家登录管理系统");
        webLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        webLabel.setForeground(ColorManager._28_102_220);
        return webLabel;
    }

    private WebPanel createLoginPanel() {
        WebPanel webPanel = new WebPanel();
        webPanel.setLayout(new GridBagLayout());
        webPanel.setOpaque(false);
        SwingUtil.setupComponent(webPanel, createRoundPanel(), 0, 0, 1, 1);
        return webPanel;
    }

    private WebPanel createRoundPanel() {
        WebPanel webPanel = new WebPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        webPanel.setLayout(new GridBagLayout());
        webPanel.setMargin(20);
        SwingUtil.setupComponent(webPanel, createTitleLabel(), 0, 0, 3, 1);
        SwingUtil.setupComponent(webPanel, staffNoField, 0, 1, 3, 1);
        SwingUtil.setupComponent(webPanel, passwordField, 0, 2, 3, 1);
        SwingUtil.setupComponent(webPanel, securityField, 0, 3, 2, 1);
        SwingUtil.setupComponent(webPanel, securityButton, 2, 3, 1, 1);
        SwingUtil.setupComponent(webPanel, loginButton, 0, 4, 3, 1);
        return webPanel;
    }

}
