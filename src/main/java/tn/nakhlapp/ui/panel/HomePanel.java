package tn.nakhlapp.ui.panel;

import tn.nakhlapp.ui.theme.UiTheme;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = UiTheme.sectionTitle("منظومة التصرف في التمور");
        title.setHorizontalAlignment(SwingConstants.RIGHT);
        add(title, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "مرحباً بكم في النسخة الحديثة من NAKHLA.\n\n"
                        + "• واجهة موحدة مع قائمة جانبية\n"
                        + "• اتصال آمن بقاعدة البيانات (HikariCP + PreparedStatement)\n"
                        + "• نفس الجداول والمنطق التجاري (عمليات، تسويات، أقفاص)\n"
                        + "• تصحيح أخطاء الاتصال في النسخة القديمة\n\n"
                        + "اختر قسماً من القائمة للبدء."
        );
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(UiTheme.BODY);
        info.setBackground(getBackground());
        add(info, BorderLayout.CENTER);
    }
}
