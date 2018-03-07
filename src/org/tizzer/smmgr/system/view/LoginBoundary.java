package org.tizzer.smmgr.system.view;

import com.alee.extended.image.DisplayType;
import com.alee.extended.image.WebImage;
import com.alee.extended.layout.TableLayout;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import org.tizzer.smmgr.system.component.WebBSButton;
import org.tizzer.smmgr.system.component.WebBSPasswordField;
import org.tizzer.smmgr.system.component.WebBSTextField;
import org.tizzer.smmgr.system.constant.ResultCode;
import org.tizzer.smmgr.system.constant.RuntimeConstants;
import org.tizzer.smmgr.system.manager.ColorManager;
import org.tizzer.smmgr.system.manager.FontManager;
import org.tizzer.smmgr.system.manager.IconManager;
import org.tizzer.smmgr.system.model.request.LoginRequestDto;
import org.tizzer.smmgr.system.model.response.LoginResponseDto;
import org.tizzer.smmgr.system.resolver.HttpResolver;
import org.tizzer.smmgr.system.template.Initialization;
import org.tizzer.smmgr.system.util.GridBagUtil;
import org.tizzer.smmgr.system.util.TimeUtil;
import org.tizzer.smmgr.system.util.ToolTipUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author tizzer
 * @version 1.0
 */
public class LoginBoundary extends Initialization {

    private WebImage image1;
    private WebImage image2;
    private WebImage image3;
    private ComponentTransition animation$component;
    private WebTextField field$account;
    private WebPasswordField field$password;
    private WebTextField field$security;
    private WebButton button$security;
    private WebButton button$login;

    public LoginBoundary() {
        super();
    }

    @Override
    public void initProp() {
        setMargin(150);
        setLayout(new TableLayout(new double[][]{
                {TableLayout.FILL, TableLayout.PREFERRED},
                {TableLayout.FILL}
        }));
    }

    @Override
    public void initVal() {
        image1 = new WebImage(IconManager._ICON_POSTER1);
        image1.setDisplayType(DisplayType.fitComponent);
        image2 = new WebImage(IconManager._ICON_POSTER2);
        image2.setDisplayType(DisplayType.fitComponent);
        image3 = new WebImage(IconManager._ICON_POSTER3);
        image3.setDisplayType(DisplayType.fitComponent);
        animation$component = new ComponentTransition();
        animation$component.setOpaque(false);
        animation$component.setContent(image1);
        animation$component.addTransitionEffect(createFadeTransitionEffect());
        field$account = new WebBSTextField(15);
        field$account.setFieldMargin(0, 6, 0, 0);
        field$account.setLeadingComponent(new WebImage(IconManager._ICON_ACCOUNT));
        field$account.setInputPrompt("请输入账号");
        field$password = new WebBSPasswordField(15);
        field$password.setFieldMargin(0, 6, 0, 0);
        field$password.setLeadingComponent(new WebImage(IconManager._ICON_PASSWORD));
        field$password.setInputPrompt("请输入密码");
        field$security = new WebBSTextField(8);
        field$security.setFieldMargin(0, 6, 0, 0);
        field$security.setLeadingComponent(new WebImage(IconManager._ICON_SECURITY));
        field$security.setInputPrompt("请输入验证码");
        button$security = new WebBSButton(getSecurityCode(), WebBSButton.BLUE);
        button$login = new WebBSButton("登录", WebBSButton.BLUE);
    }

    @Override
    public void initView() {
        add(new WebPanel() {{
            setOpaque(false);
            add(animation$component);
        }}, "0,0");
        add(new WebPanel(new GridBagLayout()) {{
            setOpaque(false);
            GridBagUtil.setupComponent(this, new WebPanel() {
                {
                    setLayout(new GridBagLayout());
                    setMargin(20);
                    GridBagUtil.setupComponent(this, new WebLabel() {{
                        setHorizontalAlignment(WebLabel.CENTER);
                        setText("超市管家登录管理系统");
                        setFont(FontManager._FONT_IMPORTANT);
                        setForeground(ColorManager._28_102_220);
                    }}, 0, 0, 3, 1);
                    GridBagUtil.setupComponent(this, field$account, 0, 1, 3, 1);
                    GridBagUtil.setupComponent(this, field$password, 0, 2, 3, 1);
                    GridBagUtil.setupComponent(this, field$security, 0, 3, 2, 1);
                    GridBagUtil.setupComponent(this, button$security, 2, 3, 1, 1);
                    GridBagUtil.setupComponent(this, button$login, 0, 4, 3, 1);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
            }, 0, 0, 1, 1);
        }}, "1,0");
        startAnimation();
    }

    @Override
    public void initAction() {
        button$security.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button$security.setText(getSecurityCode());
            }
        });

        button$login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO for dev (need del in prod)
                enter();
                updateRuntimeParam(TimeUtil.getCurrentTime());
                if (true)
                    return;
                //TODO for prod
                localVerify();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradientPaint = new GradientPaint(0, 0, Color.ORANGE, getWidth() * 3 / 4, getHeight() * 3 / 4, Color.GREEN, false);
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * 创建渐变动画
     *
     * @return
     */
    private FadeTransitionEffect createFadeTransitionEffect() {
        FadeTransitionEffect fadeTransitionEffect = new FadeTransitionEffect();
        fadeTransitionEffect.setMinimumSpeed(0.03f);
        fadeTransitionEffect.setSpeed(0.1f);
        return fadeTransitionEffect;
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (animation$component.getContent() == image1) {
                    animation$component.performTransition(image2);
                } else if (animation$component.getContent() == image2) {
                    animation$component.performTransition(image3);
                } else {
                    animation$component.performTransition(image1);
                }
                startAnimation();
            }
        }, 3000);
    }

    /**
     * 设置全局默认按钮
     */
    public void setDefaultButton() {
        getRootPane().setDefaultButton(button$login);
    }

    /**
     * 获取安全码
     *
     * @return
     */
    public String getSecurityCode() {
        String code = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            code += random.nextInt(10) + " ";
        }
        code += random.nextInt(10);
        return code;
    }

    /**
     * 执行本地验证
     */
    private void localVerify() {
        String account = field$account.getText();
        if (account.equals("")) {
            ToolTipUtil.showTip(button$login, "账号不能为空");
            return;
        }
        String password = field$password.getText();
        if (password.equals("")) {
            ToolTipUtil.showTip(button$login, "密码不能为空");
            return;
        }
        String securityCode = button$security.getText().replaceAll(" ", "");
        if (!field$security.getText().equals(securityCode.replaceAll(" ", ""))) {
            button$security.setText(getSecurityCode());
            ToolTipUtil.showTip(button$login, "验证码不正确");
            return;
        }
        try {
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setAccount(account);
            loginRequestDto.setPassword(password);
            LoginResponseDto loginResponseDto = HttpResolver.post("/login", loginRequestDto.toString(), LoginResponseDto.class);
            if (loginResponseDto.getCode() == ResultCode.ERROR) {
                button$security.setText(getSecurityCode());
                ToolTipUtil.showTip(button$login, loginResponseDto.getMessage());
                return;
            }
            if (loginResponseDto.getCode() == ResultCode.OK) {
                //TODO for up to identity enter homepage
                enter();
                updateRuntimeParam(TimeUtil.getCurrentTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入主界面
     */
    private void enter() {
        Container parent = getParent();
        parent.removeAll();
        parent.add(new HomeBoundary());
        parent.validate();
        parent.repaint();
    }

    /**
     * @param date
     */
    private void updateRuntimeParam(String date) {
        RuntimeConstants.isLogin = true;
        RuntimeConstants.login_at = date;
    }

}
