package org.tizzer.smmgr.system;

import com.alee.extended.layout.TableLayout;
import com.alee.extended.progress.WebStepProgress;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.Direction;
import com.alee.extended.transition.effects.slide.SlideTransitionEffect;
import com.alee.extended.transition.effects.slide.SlideType;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import org.tizzer.smmgr.system.constant.IconManager;
import org.tizzer.smmgr.system.handler.XMLHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author tizzer
 * @version 1.0
 */
public class GuideApplication extends WebDialog implements ActionListener {

    private Image image = IconManager.GUIDE;
    private WebStepProgress stepProgress;
    private ComponentTransition transition;
    private JComponent[] guides;
    private WebButton previousButton;
    private WebButton nextButton;
    private int currentIndex = 0;

    GuideApplication() {
        stepProgress = createStepProgress();
        transition = createAnimationComponent();
        guides = new JComponent[]{createFirstGuide(), createSecondGuide(), createThirdGuide()};
        previousButton = createSwitchButton(IconManager.PREVIOUS, IconManager.PREVIOUSOVER, IconManager.PREVIOUSPRESS);
        previousButton.setVisible(false);
        nextButton = createSwitchButton(IconManager.NEXT, IconManager.NEXTOVER, IconManager.NEXTPRESS);
        previousButton.addActionListener(this);
        nextButton.addActionListener(this);

        this.add(createGuidePane(previousButton, transition, nextButton, stepProgress));
        this.setUndecorated(true);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setSize(image.getWidth(this), image.getHeight(this));
        this.setLocationRelativeTo(this);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(previousButton)) {
            ((SlideTransitionEffect) transition.getTransitionEffect()).setDirection(Direction.right);
            switchView(--currentIndex);
        } else {
            ((SlideTransitionEffect) transition.getTransitionEffect()).setDirection(Direction.left);
            switchView(++currentIndex);
        }
    }

    private void switchView(int index) {
        previousButton.setVisible(index != 0);
        nextButton.setVisible(index != 2);
        transition.performTransition(guides[index]);
        stepProgress.setSelectedStepIndex(index);
    }

    private WebPanel createGuidePane(JComponent... component) {
        WebPanel webPanel = new WebPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        double border = 20;
        double space = 0.20;
        webPanel.setLayout(new TableLayout(new double[][]{
                {border, space, border, TableLayout.FILL, border, space, border},
                {border, space, border, TableLayout.FILL, border, space, border}
        }));
        webPanel.add(component[0], "1, 3, c, c");
        webPanel.add(component[1], "3, 3, c, c");
        webPanel.add(component[2], "5, 3, c, c");
        webPanel.add(component[3], "1, 5, 5, 1");
        return webPanel;
    }

    private WebLabel createFirstGuide() {
        WebLabel webLabel = new WebLabel();
        webLabel.setText("<html>" +
                "<h2 align=center color=white face='Microsoft YaHei'>默认用户说明</h2>" +
                "<h3 align=center color=white face='Microsoft YaHei'>管理员账号密码均为1001</h3>" +
                "<h3 align=center color=white face='Microsoft YaHei'>建议进入系统后马上修改</h3>" +
                "<h3 align=center color=white face='Microsoft YaHei'>修改步骤：员工管理→编辑</h3>" +
                "<html>");
        webLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return webLabel;
    }

    private WebLabel createSecondGuide() {
        WebLabel webLabel = new WebLabel();
        webLabel.setText("<html>" +
                "<h2 align=center color=white face='Microsoft YaHei'>默认店铺说明</h2>" +
                "<h3 align=center color=white face='Microsoft YaHei'>店铺名为default</h3>" +
                "<h3 align=center color=white face='Microsoft YaHei'>建议进入系统后马上修改</h3>" +
                "<h3 align=center color=white face='Microsoft YaHei'>修改步骤：门店管理→编辑</h3>" +
                "<html>");
        webLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return webLabel;
    }

    private WebButton createThirdGuide() {
        WebButton webButton = createSwitchButton(IconManager.ENTER, IconManager.ENTEROVER, IconManager.ENTERPRESS);
        webButton.addActionListener(e -> {
            XMLHandler xmlHandler = new XMLHandler("data-init.xml");
            xmlHandler.putElementValue("guide", true).commit();
            GuideApplication.this.dispose();
            SwingUtilities.invokeLater(SmmgrApplication::new);
        });
        return webButton;
    }

    private ComponentTransition createAnimationComponent() {
        ComponentTransition componentTransition = new ComponentTransition();
        componentTransition.setOpaque(false);
        componentTransition.setContent(createFirstGuide());
        componentTransition.addTransitionEffect(createSlideTransitionEffect());
        return componentTransition;
    }

    private WebStepProgress createStepProgress() {
        WebStepProgress webStepProgress = new WebStepProgress(3);
        webStepProgress.setDisabledProgressColor(Color.WHITE);
        webStepProgress.setProgressColor(Color.PINK);
        return webStepProgress;
    }

    private WebButton createSwitchButton(ImageIcon icon, ImageIcon overIcon, ImageIcon pressIcon) {
        WebButton webButton = new WebButton();
        webButton.setIcon(icon);
        webButton.setRolloverIcon(overIcon);
        webButton.setPressedIcon(pressIcon);
        webButton.setMoveIconOnPress(false);
        webButton.setUndecorated(true);
        return webButton;
    }

    private SlideTransitionEffect createSlideTransitionEffect() {
        SlideTransitionEffect effect = new SlideTransitionEffect();
        effect.setFade(false);
        effect.setType(SlideType.moveBoth);
        effect.setDirection(Direction.left);
        return effect;
    }

}
