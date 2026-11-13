import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
/** SkillSight: standalone Java desktop skill tracker. Run with: java SkillSight.java */
public class SkillSight {
    private static final float LEFT=Component.LEFT_ALIGNMENT;
    private static final Color NAVY=new Color(15,24,48), NAVY2=new Color(25,38,70), BG=new Color(244,246,251), BLUE=new Color(79,99,239), INK=new Color(27,37,58), MUTED=new Color(107,118,140), WHITE=Color.WHITE, GREEN=new Color(24,145,105), RED=new Color(190,61,76);
    private static final Path STORE=Path.of(System.getProperty("user.home"),".skillsight","skillsight.data");
    private static final String[] CATEGORIES= {
        "Programming","Computer Science","Data & Databases","Web Development","Soft Skills"
    }
    ;
    private static final String[][] CATALOG= {
        {
            "Java","Python","Object-Oriented Programming","Spring Boot","Software Development"
        }
        , {
            "Data Structures and Algorithms","Problem Solving"
        }
        , {
            "Database Management Systems","Data Analysis"
        }
        , {
            "Web Development","Git and Version Control"
        }
        , {
            "Communication","Teamwork","Time Management","Critical Thinking","Leadership","Presentation Skills","Adaptability"
        }
    }
    ;
    private static final String[] CAREERS= {
        "Java Developer","Backend Developer","Full-Stack Developer","Data Analyst","Software Tester","Data Engineer","Cybersecurity Analyst"
    }
    ;
    private static final int[] RESULT_THRESHOLDS={90,75,60,40};
    private static final DateTimeFormatter DATE=DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm");
    private Data data;
    private Account active;
    private JFrame frame;
    private JPanel body,sidebar;
    private JLabel pageTitle,toast;
    private String page="Dashboard";
    private String analyticsFocusSkill;
    private JTextField skillSearch;
    private javax.swing.Timer assessmentTimer;
    private JLabel assessmentClock;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new SkillSight().start());
    }
    private void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception ignored) {
        }
        data=load();
        for(Account account:data.accounts.values()) {
            if(account.quizHistory==null)account.quizHistory=new ArrayList<>();
        }
        showLogin();
    }
    private Data load() {
        Object saved=LocalDataStore.load(STORE);
        return saved instanceof Data existing?existing:new Data();
    }
    private void save() {
        try {
            LocalDataStore.save(data,STORE);
        }
        catch(Exception e) {
            message("Could not save local data: "+e.getMessage(),RED);
        }
    }
    private void showLogin() {
        frame=new JFrame("SkillSight — Student Skill Analyzer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1024,700));
        frame.setSize(1240,800);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(authPanel(false));
        frame.setVisible(true);
    }
    private JPanel authPanel(boolean register) {
        JPanel root=new JPanel(new GridBagLayout());
        root.setBackground(BG);
        JPanel card=new JPanel(new GridLayout(1,2));
        card.setPreferredSize(new Dimension(register?930:960,register?680:600));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(231,235,244)),BorderFactory.createEmptyBorder(0,0,0,0)));
        JPanel brand=new JPanel();
        brand.setBackground(NAVY);
        brand.setLayout(new BoxLayout(brand,BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(48,42,42,38));
        JLabel logo=label("◈  SKILLSIGHT",20,BLUE,true);
        JLabel headline=label("Understand your strengths. Build your next step.",30,WHITE,true);
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);
        headline.setMaximumSize(new Dimension(350,150));
        JLabel sub=label("A personal skill analyzer and career development companion.",15,new Color(190,201,224),false);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(logo);
        brand.add(Box.createVerticalStrut(36));
        brand.add(headline);
        brand.add(Box.createVerticalStrut(18));
        brand.add(sub);
        brand.add(Box.createVerticalGlue());
        brand.add(label("Assess  ·  Learn  ·  Grow",14,new Color(190,201,224),false));
        JPanel form=new JPanel();
        form.setBackground(WHITE);
        form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(register?38:58,48,38,48));
        form.add(label(register?"Create your student account":"Welcome back",27,INK,true));
        form.add(Box.createVerticalStrut(8));
        form.add(label(register?"Your skill data stays on this computer.":"Sign in to continue your journey.",14,MUTED,false));
        form.add(Box.createVerticalStrut(25));
        JTextField name=register?field("Full name"):null,email=field("Email address"),college=register?field("College or university"):null,course=register?field("Course or department"):null;
        JPasswordField pass=passwordField("Password"),confirm=register?passwordField("Confirm password"):null;
        JComboBox<String> year=register?new JComboBox<>(yearOptions()):null;
        if(register) {
            addField(form,"Full name",name);
            addField(form,"Email",email);
            addField(form,"Password",pass);
            addField(form,"Confirm password",confirm);
            addField(form,"College / University",college);
            addField(form,"Course / Department",course);
            addField(form,"Study year",year);
        }
        else {
            addField(form,"Email",email);
            addField(form,"Password",pass);
        }
        JLabel error=label("",13,RED,false);
        error.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(error);
        form.add(Box.createVerticalStrut(12));
        JButton submit=button(register?"Create account":"Sign in",BLUE,WHITE);
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(submit);
        form.add(Box.createVerticalStrut(12));
        JButton switchBtn=link(register?"Back to sign in":"New to SkillSight?  Create an account");
        switchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(switchBtn);
        submit.addActionListener(e-> {
            try {
                String em=email.getText().trim().toLowerCase(Locale.ROOT); char[] pw=pass.getPassword(); if(register) {
                    String nm=name.getText().trim(); if(nm.length()<2||!em.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                        error.setText("Enter your name and a valid email address."); return;
                    }
                    if(pw.length<10||pw.length>72) {
                        error.setText("Use a password between 10 and 72 characters."); return;
                    }
                    if(!Arrays.equals(pw,confirm.getPassword())) {
                        error.setText("The passwords do not match."); return;
                    }
                    if(data.accounts.containsKey(em)) {
                        error.setText("An account with this email already exists."); return;
                    }
                    Account a=new Account(nm,em,PasswordHasher.hash(pw)); a.college=college.getText().trim(); a.course=course.getText().trim(); a.year=year.getSelectedIndex()+1; data.accounts.put(em,a); save(); active=a; showApp();
                }
                else {
                    Account a=data.accounts.get(em); if(a==null||!PasswordHasher.verify(pw,a.salt,a.hash)) {
                        error.setText("Email or password is incorrect."); return;
                    }
                    active=a; showApp();
                }
                Arrays.fill(pw,'\0');
            }
            catch(Exception ex) {
                error.setText("Could not continue: "+ex.getMessage());
            }
        }
        );
        switchBtn.addActionListener(e-> {
            frame.setContentPane(authPanel(!register)); frame.revalidate(); frame.repaint();
        }
        );
        root.add(card);
        card.add(brand);
        JScrollPane formScroll=new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getViewport().setBackground(WHITE);
        card.add(formScroll);
        return root;
    }
    private void addField(JPanel p,String title,JComponent input) {
        p.add(label(title,12,INK,true));
        p.add(Box.createVerticalStrut(5));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(input);
        p.add(Box.createVerticalStrut(11));
    }
    private JTextField field(String hint) {
        JTextField t=new JTextField();
        t.putClientProperty("JTextField.placeholderText",hint);
        t.setFont(new Font("Segoe UI",0,14));
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218,224,235)),new EmptyBorder(8,11,8,11)));
        return t;
    }
    private JPasswordField passwordField(String hint) {
        JPasswordField t=new JPasswordField();
        t.setFont(new Font("Segoe UI",0,14));
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218,224,235)),new EmptyBorder(8,11,8,11)));
        return t;
    }
    private void showApp() {
        frame.setTitle("SkillSight — "+active.name);
        JPanel shell=new JPanel(new BorderLayout());
        sidebar=new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(235,0));
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25,14,18,14));
        sidebar.add(label("◈  SKILLSIGHT",20,new Color(128,145,255),true));
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(label(active.name,15,WHITE,true));
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(label("STUDENT  ·  LOCAL PROFILE",10,new Color(173,187,220),true));
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(14));
        for(String item:List.of("Dashboard","My Skills","Assessment","Analytics","Career Explorer","Learning Roadmap","Assessment History","Reports","My Profile","Settings")) {
            JButton b=nav(item);
            b.addActionListener(e->showPage(item));
            sidebar.add(b);
        }
        sidebar.add(Box.createVerticalGlue());
        JButton logout=nav("Log out");
        logout.addActionListener(e-> {
            save(); frame.setContentPane(authPanel(false)); frame.setTitle("SkillSight — Student Skill Analyzer"); frame.revalidate();
        }
        );
        sidebar.add(logout);
        JPanel main=new JPanel(new BorderLayout());
        main.setBackground(BG);
        JPanel top=new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(24,32,12,32));
        pageTitle=label("Dashboard",25,INK,true);
        top.add(pageTitle,BorderLayout.WEST);
        JLabel profile=label(active.name+"   ·   Student",13,MUTED,false);
        top.add(profile,BorderLayout.EAST);
        body=new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0,32,26,32));
        toast=label("",13,GREEN,false);
        JPanel stack=new JPanel(new BorderLayout(0,9));
        stack.setOpaque(false);
        stack.add(toast,BorderLayout.NORTH);
        stack.add(body,BorderLayout.CENTER);
        main.add(top,BorderLayout.NORTH);
        main.add(stack,BorderLayout.CENTER);
        shell.add(sidebar,BorderLayout.WEST);
        shell.add(main,BorderLayout.CENTER);
        frame.setContentPane(shell);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.revalidate();
        showPage("Dashboard");
    }
    private JButton nav(String s) {
        JButton b=new JButton("  "+icon(s)+"   "+s);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(new Color(24,35,59));
        b.setBackground(new Color(250,251,255));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,226,239)),new EmptyBorder(10,11,10,7)));
        b.setFocusPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(226,233,255));
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(250,251,255));
            }
        }
        );
        return b;
    }
    private String icon(String s) {
        return switch(s) {
            case "Dashboard"->"▦";
            case "My Skills"->"◉";
            case "Assessment"->"✓";
            case "Analytics"->"▥";
            case "Career Explorer"->"◇";
            case "Learning Roadmap"->"➜";
            case "Assessment History"->"◷";
            case "Reports"->"▤";
            case "My Profile"->"●";
            case "Settings"->"⚙";
            default->"↪";
        }
        ;
    }
    private void showPage(String selected) {
        page=selected;
        pageTitle.setText(selected);
        toast.setText(" ");
        JPanel panel=switch(selected) {
            case "Dashboard"->dashboard();
            case "My Skills"->mySkills();
            case "Assessment"->assessment();
            case "Analytics"->analytics();
            case "Career Explorer"->careers();
            case "Learning Roadmap"->roadmap();
            case "Assessment History"->history();
            case "Reports"->reports();
            case "My Profile"->profile();
            case "Settings"->settings();
            default->dashboard();
        }
        ;
        body.removeAll();
        body.add(panel,BorderLayout.CENTER);
        body.revalidate();
        body.repaint();
    }
    private JPanel dashboard() {
        JPanel p=pagePanel();
        p.setLayout(new BorderLayout(0,18));
        JPanel head=new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(label("Welcome back, "+first(active.name)+" 👋",23,INK,true),BorderLayout.NORTH);
        head.add(label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))+"   ·   Small steps build lasting skills.",13,MUTED,false),BorderLayout.SOUTH);
        List<Skill> skills=skills();
        long count=skills.stream().filter(s->!s.history.isEmpty()).count();
        double avg=skills.stream().filter(s->!s.history.isEmpty()).mapToDouble(Skill::current).average().orElse(0);
        long gaps=skills.stream().filter(s->!s.history.isEmpty()&&s.gap()>0).count();
        JPanel cards=new JPanel(new GridLayout(1,4,14,0));
        cards.setOpaque(false);
        cards.add(stat("ASSESSED SKILLS",count+" / "+skills.size(),"Keep your profile current"));
        cards.add(stat("AVERAGE SKILL SCORE",String.format(Locale.ROOT,"%.0f%%",avg),"Across skills with saved results"));
        cards.add(stat("OPEN SKILL GAPS",String.valueOf(gaps),"Based on your own targets"));
        cards.add(stat("ASSESSMENT ATTEMPTS",String.valueOf(attempts()),"All attempts saved locally"));
        JPanel top=new JPanel(new BorderLayout(0,20));
        top.setOpaque(false);
        top.add(head,BorderLayout.NORTH);
        top.add(cards,BorderLayout.CENTER);
        JPanel center=new JPanel(new GridLayout(1,2,16,0));
        center.setOpaque(false);
        center.add(chartCard(skills));
        center.add(insights(skills));
        p.add(top,BorderLayout.NORTH);
        p.add(center,BorderLayout.CENTER);
        return p;
    }
    private JPanel stat(String cap,String value,String hint) {
        JPanel c=card();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.add(label(cap,11,MUTED,true));
        c.add(Box.createVerticalStrut(10));
        c.add(label(value,27,BLUE,true));
        c.add(Box.createVerticalStrut(5));
        c.add(label(hint,12,MUTED,false));
        return c;
    }
    private JPanel chartCard(List<Skill> ss) {
        JPanel c=card();
        c.setLayout(new BorderLayout());
        c.add(label("Your latest skill scores",16,INK,true),BorderLayout.NORTH);
        SkillChart chart=new SkillChart(ss);
        JScrollPane scroll=new JScrollPane(chart);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        c.add(scroll,BorderLayout.CENTER);
        return c;
    }
    private JPanel insights(List<Skill> ss) {
        JPanel c=card();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.add(label("Your next best steps",16,INK,true));
        c.add(Box.createVerticalStrut(18));
        List<Skill> assessed=ss.stream().filter(s->!s.history.isEmpty()).sorted(Comparator.comparingDouble(Skill::gap).reversed()).toList();
        if(assessed.isEmpty()) {
            c.add(label("Your dashboard is ready. Complete your first self-assessment to see your strengths and skill gaps here.",14,MUTED,false));
        }
        else {
            for(Skill s:assessed.stream().limit(4).toList()) {
                c.add(label((s.gap()>0?"Focus next  ·  ":"Doing well  ·  ")+s.name,14,INK,true));
                c.add(label(String.format(Locale.ROOT,"Current %.0f  /  target %.0f  ·  %s",s.current(),s.target,s.level()),12,MUTED,false));
                c.add(Box.createVerticalStrut(14));
            }
        }
        c.add(Box.createVerticalGlue());
        JButton b=button("Start an assessment",BLUE,WHITE);
        b.addActionListener(e->showPage("Assessment"));
        c.add(b);
        return c;
    }
    private JPanel insightsDummy() {
        return card();
    }
    private JPanel pagePanel() {
        JPanel p=new JPanel();
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(2,0,0,0));
        return p;
    }
    private JPanel card() {
        JPanel p=new JPanel();
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(231,235,243)),new EmptyBorder(18,18,18,18)));
        return p;
    }
    private JPanel mySkills() {
        JPanel p=pagePanel();
        p.setLayout(new BorderLayout(0,12));
        JPanel bar=new JPanel(new BorderLayout(12,0));
        bar.setOpaque(false);
        skillSearch=field("Search skill or category");
        skillSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                refreshSkillsTable((JTable)p.getClientProperty("table"),skillSearch.getText());
            }
        }
        );
        bar.add(skillSearch,BorderLayout.CENTER);
        JButton assess=button("＋  Assess a skill",BLUE,WHITE);
        assess.addActionListener(e->showPage("Assessment"));
        bar.add(assess,BorderLayout.EAST);
        JTable t=skillTable();
        p.putClientProperty("table",t);
        p.add(bar,BorderLayout.NORTH);
        p.add(new JScrollPane(t),BorderLayout.CENTER);
        return p;
    }
    private JTable skillTable() {
        String[] cols= {
            "Skill","Category","Current","Level","Target","Gap","Last assessed"
        }
        ;
        DefaultTableModel m=new DefaultTableModel(cols,0) {
            public boolean isCellEditable(int r,int c) {
                return c==4;
            }
        }
        ;
        for(Skill s:skills())m.addRow(new Object[] {
            s.name,s.category,s.history.isEmpty()?"—":String.format("%.0f%%",s.current()),s.history.isEmpty()?"Not assessed":s.level(),s.target+"%",s.history.isEmpty()?"—":String.format("%.0f",s.gap()),s.history.isEmpty()?"—":s.history.get(0).at.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }
        );
        JTable t=new JTable(m);
        styleTable(t);
        t.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override public boolean stopCellEditing() {
                int row=t.getEditingRow(); try {
                    double v=Double.parseDouble((String)getCellEditorValue().toString().replace("%","")); checkScore(v); Skill s=skills().stream().filter(x->x.name.equals(t.getValueAt(row,0))).findFirst().orElseThrow(); s.target=v; save(); return super.stopCellEditing();
                }
                catch(Exception e) {
                    Toolkit.getDefaultToolkit().beep(); return false;
                }
            }
        }
        );
        t.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2&&t.getSelectedRow()>=0) {
                    String n=(String)t.getValueAt(t.getSelectedRow(),0); showPage("Assessment");
                }
            }
        }
        );
        return t;
    }
    private void refreshSkillsTable(JTable t,String q) {
        if(t==null)return;
        DefaultTableModel m=(DefaultTableModel)t.getModel();
        m.setRowCount(0);
        String term=q.toLowerCase(Locale.ROOT);
        for(Skill s:skills())if(s.name.toLowerCase(Locale.ROOT).contains(term)||s.category.toLowerCase(Locale.ROOT).contains(term))m.addRow(new Object[] {
            s.name,s.category,s.history.isEmpty()?"—":String.format("%.0f%%",s.current()),s.history.isEmpty()?"Not assessed":s.level(),s.target+"%",s.history.isEmpty()?"—":String.format("%.0f",s.gap()),s.history.isEmpty()?"—":s.history.get(0).at.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }
        );
    }
    private JPanel assessment() {
        if(active.pendingQuiz!=null)return quizPanel(active.pendingQuiz);
        if(active.lastQuiz!=null)return quizResultPanel(active.lastQuiz);

        JPanel page=pagePanel();
        page.setLayout(new GridBagLayout());
        JPanel card=card();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(650,460));
        card.add(label("Skill knowledge assessment",24,INK,true));
        card.add(Box.createVerticalStrut(8));
        card.add(label("Choose a skill to begin a 10-question check across three difficulty levels.",14,MUTED,false));
        card.add(Box.createVerticalStrut(22));

        JComboBox<String> selectedSkill=new JComboBox<>(skills().stream().map(s->s.name).toArray(String[]::new));
        addField(card,"Skill",selectedSkill);
        String skillName=(String)selectedSkill.getSelectedItem();
        Skill skill=findSkill(skillName);
        String latest=skill==null||skill.history.isEmpty()?"No completed assessments yet":
                String.format(Locale.ROOT,"Latest score: %.0f%% · %d attempts",skill.current(),skill.history.size());
        JLabel latestLabel=label(latest,13,MUTED,false);
        latestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(latestLabel);
        selectedSkill.addActionListener(e->{
            Skill chosen=findSkill((String)selectedSkill.getSelectedItem());
            latestLabel.setText(chosen==null||chosen.history.isEmpty()?"No completed assessments yet":
                    String.format(Locale.ROOT,"Latest score: %.0f%% · %d attempts",chosen.current(),chosen.history.size()));
        });

        card.add(Box.createVerticalStrut(20));
        card.add(label("Assessment format",14,INK,true));
        card.add(Box.createVerticalStrut(7));
        card.add(label("10 multiple-choice questions · 3 easy · 4 medium · 3 difficult",13,MUTED,false));
        card.add(label("20-minute timer · one mark per question · answers saved as you go",13,MUTED,false));
        card.add(label("Questions are skill-specific. Correct answers appear after submission.",13,MUTED,false));
        card.add(Box.createVerticalGlue());
        JButton start=button("Start assessment",BLUE,WHITE);
        start.setAlignmentX(Component.LEFT_ALIGNMENT);
        start.addActionListener(e->startQuiz((String)selectedSkill.getSelectedItem()));
        card.add(start);
        page.add(card);
        return page;
    }

    private void startQuiz(String skill) {
        if(skill==null)return;
        if(active.quizHistory==null)active.quizHistory=new ArrayList<>();
        if(JOptionPane.showConfirmDialog(frame,
                "Start the 20-minute assessment for "+skill+"? You can navigate between questions, and your answers will be saved locally.",
                "Begin assessment",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        QuizSession session=new QuizSession(skill,AssessmentQuestionBank.create(skill,System.nanoTime()));
        if(session.questions.size()!=10){message("A complete 10-question set is not available for this skill yet.",RED);return;}
        session.attemptNumber=(int)active.quizHistory.stream().filter(item->item.skill.equals(skill)).count()+1;
        active.pendingQuiz=session;
        active.lastQuiz=null;
        save();
        showPage("Assessment");
    }

    private JPanel quizPanel(QuizSession session) {
        JPanel outer=pagePanel();
        outer.setLayout(new GridBagLayout());
        JPanel card=card();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(820,620));
        JPanel heading=new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(label(session.skill+" assessment",21,INK,true),BorderLayout.WEST);
        assessmentClock=label("20:00 remaining",16,BLUE,true);
        heading.add(assessmentClock,BorderLayout.EAST);
        card.add(heading);
        card.add(Box.createVerticalStrut(10));

        int questionIndex=session.currentIndex;
        AssessmentQuestionBank.Question question=session.questions.get(questionIndex);
        JProgressBar progress=new JProgressBar(0,session.questions.size());
        progress.setValue(questionIndex+1);
        progress.setStringPainted(true);
        progress.setString("Question "+(questionIndex+1)+" of "+session.questions.size());
        card.add(progress);
        JLabel answeredStatus=label(answeredCount(session)+" of 10 answered · your selections save automatically",12,MUTED,false);
        card.add(answeredStatus);
        card.add(Box.createVerticalStrut(8));
        Color difficultyColor=switch(question.difficulty()){case "Easy"->GREEN;case "Medium"->BLUE;default->new Color(171,86,43);};
        card.add(label(question.difficulty()+"  ·  "+question.topic()+"  ·  "+question.marks()+" mark",12,difficultyColor,true));
        card.add(Box.createVerticalStrut(8));
        JTextArea prompt=new JTextArea(question.prompt());
        prompt.setWrapStyleWord(true);
        prompt.setLineWrap(true);
        prompt.setEditable(false);
        prompt.setOpaque(false);
        prompt.setFont(new Font("Segoe UI",Font.BOLD,17));
        prompt.setForeground(INK);
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(prompt);
        card.add(Box.createVerticalStrut(15));

        ButtonGroup answers=new ButtonGroup();
        for(int i=0;i<question.options().size();i++) {
            final int answer=i;
            JRadioButton option=new JRadioButton(question.options().get(i));
            option.setOpaque(false);
            option.setForeground(INK);
            option.setFont(new Font("Segoe UI",Font.PLAIN,14));
            option.setAlignmentX(Component.LEFT_ALIGNMENT);
            answers.add(option);
            card.add(option);
            card.add(Box.createVerticalStrut(8));
            if(session.answers[questionIndex]==i)option.setSelected(true);
            option.addActionListener(e->{
                session.answers[questionIndex]=answer;
                answeredStatus.setText(answeredCount(session)+" of 10 answered · your selections save automatically");
                active.pendingQuiz=session;
                save();
            });
        }
        card.add(Box.createVerticalGlue());
        JPanel controls=new JPanel(new BorderLayout());
        controls.setOpaque(false);
        controls.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
        JPanel left=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        left.setOpaque(false);
        JButton previous=button("Previous",new Color(238,241,255),BLUE);
        previous.setPreferredSize(new Dimension(150,46));
        previous.setEnabled(questionIndex>0);
        previous.addActionListener(e->{session.currentIndex--;save();showPage("Assessment");});
        left.add(previous);
        JButton next=button(questionIndex==session.questions.size()-1?"Submit assessment":"Next",BLUE,WHITE);
        next.setPreferredSize(new Dimension(170,46));
        next.addActionListener(e->{
            if(questionIndex<session.questions.size()-1)session.currentIndex++;
            else showQuizConfirmation(session);
            save();
            if(questionIndex<session.questions.size()-1)showPage("Assessment");
        });
        controls.add(left,BorderLayout.WEST);
        controls.add(next,BorderLayout.EAST);
        card.add(controls);
        outer.add(card);
        startAssessmentTimer(session);
        return outer;
    }

    private long answeredCount(QuizSession session) {
        return Arrays.stream(session.answers).filter(answer->answer>=0).count();
    }

    private void startAssessmentTimer(QuizSession session) {
        if(assessmentTimer!=null)assessmentTimer.stop();
        assessmentTimer=new javax.swing.Timer(1000,e->{
            long elapsed=Duration.between(session.startedAt,LocalDateTime.now()).getSeconds();
            long remaining=Math.max(0,session.durationSeconds-elapsed);
            if(assessmentClock!=null)assessmentClock.setText(String.format(Locale.ROOT,"%02d:%02d remaining",remaining/60,remaining%60));
            if(remaining==0)completeQuiz(session,true);
        });
        assessmentTimer.start();
    }

    private void showQuizConfirmation(QuizSession session) {
        int unanswered=(int)Arrays.stream(session.answers).filter(answer->answer<0).count();
        String text=unanswered==0?"Submit your answers now?":"You have "+unanswered+" unanswered question(s). Submit anyway?";
        if(JOptionPane.showConfirmDialog(frame,text,"Submit assessment",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
            completeQuiz(session,false);
    }

    private void completeQuiz(QuizSession session,boolean timedOut) {
        if(session.completedAt!=null)return;
        AssessmentScorer.Result result=AssessmentScorer.calculate(session.questions,session.answers);
        session.correct=result.correct();
        session.attempted=result.attempted();
        session.incorrect=result.incorrect();
        session.unanswered=result.unanswered();
        session.marksObtained=result.marksObtained();
        session.totalMarks=result.totalMarks();
        session.score=(int)Math.round(result.percentage());
        session.accuracy=result.accuracy();
        session.elapsedSeconds=(int)Math.min(session.durationSeconds,Duration.between(session.startedAt,LocalDateTime.now()).getSeconds());
        session.topicResults=result.topicResults();
        session.levelResults=result.levelResults();
        session.completedAt=LocalDateTime.now();
        if(active.quizHistory==null)active.quizHistory=new ArrayList<>();
        session.previousScore=active.quizHistory.stream().filter(item->item.skill.equals(session.skill)&&item.completedAt!=null)
                .max(Comparator.comparing(item->item.completedAt)).map(item->item.score).orElse(-1);
        RoadTask roadmapTask=active.roadmap.computeIfAbsent(session.skill,RoadTask::new);
        roadmapTask.recommendedTopics=session.topicResults.entrySet().stream()
                .filter(entry->entry.getValue()[0]<entry.getValue()[1]).map(Map.Entry::getKey).toList();
        Skill skill=findSkill(session.skill);
        if(skill!=null) {
            Attempt attempt=new Attempt(session.score,session.completedAt,
                    "Knowledge assessment · "+session.correct+" correct of "+session.questions.size());
            attempt.type="QUIZ";
            skill.history.add(0,attempt);
        }
        active.quizHistory.add(0,session);
        active.pendingQuiz=null;
        active.lastQuiz=session;
        if(assessmentTimer!=null)assessmentTimer.stop();
        assessmentClock=null;
        save();
        if(timedOut)JOptionPane.showMessageDialog(frame,"Time is up. Your answers have been submitted automatically.","Assessment complete",JOptionPane.INFORMATION_MESSAGE);
        showPage("Assessment");
    }

    private JPanel quizResultPanel(QuizSession session) {
        JPanel page=pagePanel();
        page.setLayout(new BorderLayout(0,12));
        JPanel summary=card();
        summary.setLayout(new BorderLayout(18,0));
        summary.add(new ScoreRing(session.score),BorderLayout.WEST);
        JPanel resultDetails=new JPanel();resultDetails.setOpaque(false);
        resultDetails.setLayout(new BoxLayout(resultDetails,BoxLayout.Y_AXIS));
        resultDetails.add(label(session.skill+" · knowledge check result · not proof of proficiency",20,INK,true));
        resultDetails.add(Box.createVerticalStrut(8));
        resultDetails.add(label(performanceCategory(session.score),22,BLUE,true));
        resultDetails.add(label("Total score: "+session.correct+" / 10 · "+session.score+"% · "+session.incorrect+" incorrect · "+session.unanswered+" unanswered",13,MUTED,false));
        resultDetails.add(label("Attempt "+session.attemptNumber+" · "+session.correct+" correct · "+session.incorrect+" incorrect · "+session.unanswered+" unanswered · "+
                String.format(Locale.ROOT,"%d of %d attempted · %.0f%% accuracy",session.attempted,session.questions.size(),session.accuracy)+" · "+formatDuration(session.elapsedSeconds),13,MUTED,false));
        JPanel difficulty=new JPanel(new FlowLayout(FlowLayout.LEFT,18,4));
        difficulty.setOpaque(false);
        for(String level:List.of("Easy","Medium","Difficult")) {
            int[] result=session.levelResults.getOrDefault(level,new int[]{0,0});
            double rate=result[1]==0?0:result[0]*100.0/result[1];
            difficulty.add(label(level+": "+result[0]+" / "+result[1]+" · "+String.format(Locale.ROOT,"%.0f%%",rate),13,INK,true));
        }
        resultDetails.add(Box.createVerticalStrut(8));
        resultDetails.add(difficulty);
        List<Map.Entry<String,int[]>> weakTopics=session.topicResults.entrySet().stream()
                .filter(entry->entry.getValue()[0]<entry.getValue()[1])
                .sorted(Comparator.comparingDouble(entry->entry.getValue()[0]/(double)entry.getValue()[1]))
                .limit(2).toList();
        String recommendation=weakTopics.isEmpty()
                ?"Strong result across the covered topics. Try the next difficulty through another retake."
                :"Next practice: "+weakTopics.stream().map(entry->entry.getKey()+" ("+entry.getValue()[0]+"/"+entry.getValue()[1]+")").collect(Collectors.joining(" · "));
        if(session.previousScore>=0&&session.score>session.previousScore)
            recommendation+=" · Improved by "+(session.score-session.previousScore)+" points; try harder practice next.";
        resultDetails.add(Box.createVerticalStrut(6));
        resultDetails.add(label("RECOMMENDED NEXT STEP  ·  "+recommendation,12,INK,false));
        summary.add(resultDetails,BorderLayout.CENTER);
        page.add(summary,BorderLayout.NORTH);

        JPanel review=new JPanel();
        review.setOpaque(false);
        review.setLayout(new BoxLayout(review,BoxLayout.Y_AXIS));
        review.add(label("Review answers and topics",17,INK,true));
        review.add(Box.createVerticalStrut(8));
        review.add(label("Topic performance",14,INK,true));
        for(Map.Entry<String,int[]> topic:session.topicResults.entrySet()) {
            int[] counts=topic.getValue();
            review.add(label(topic.getKey()+" · "+counts[0]+" / "+counts[1]+" correct · "+
                    (counts[0]==counts[1]?"Strength":"Practice recommended"),12,counts[0]==counts[1]?GREEN:RED,false));
        }
        review.add(Box.createVerticalStrut(10));
        for(int i=0;i<session.questions.size();i++) {
            AssessmentQuestionBank.Question question=session.questions.get(i);
            boolean right=session.answers[i]==question.correctOption();
            JPanel item=card();
            item.setLayout(new BoxLayout(item,BoxLayout.Y_AXIS));
            item.add(label((i+1)+". "+question.topic()+" · "+question.difficulty()+" · "+(right?"Correct":"Review this topic"),13,right?GREEN:RED,true));
            item.add(Box.createVerticalStrut(5));
            item.add(label(question.prompt().replace("\n\n"," "),12,INK,false));
            item.add(label("Your answer: "+(session.answers[i]<0?"Not answered":question.options().get(session.answers[i])),12,MUTED,false));
            item.add(label("Correct: "+question.options().get(question.correctOption()),12,GREEN,true));
            item.add(label(question.explanation(),12,MUTED,false));
            review.add(item);
            review.add(Box.createVerticalStrut(8));
        }
        page.add(new JScrollPane(review),BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        actions.setOpaque(false);
        JButton retake=button("Retake assessment",BLUE,WHITE);
        retake.addActionListener(e->{active.lastQuiz=null;showPage("Assessment");});
        JButton roadmap=button("View skill roadmap",new Color(238,241,255),BLUE);
        roadmap.addActionListener(e->{active.focusSkill=session.skill;syncRoadmap(active.career);showPage("Learning Roadmap");});
        JButton analyticsButton=button("Analytics",new Color(238,241,255),BLUE);
        analyticsButton.addActionListener(e->{analyticsFocusSkill=session.skill;showPage("Analytics");});
        actions.add(retake);actions.add(roadmap);actions.add(analyticsButton);
        page.add(actions,BorderLayout.SOUTH);
        return page;
    }

    private String performanceCategory(int score) {
        if(score>=RESULT_THRESHOLDS[0])return "Excellent";
        if(score>=RESULT_THRESHOLDS[1])return "Very Good";
        if(score>=RESULT_THRESHOLDS[2])return "Good";
        if(score>=RESULT_THRESHOLDS[3])return "Needs Improvement";
        return "Beginner · More practice recommended";
    }

    private String formatDuration(int seconds) {
        return String.format(Locale.ROOT,"%d min %02d sec",seconds/60,seconds%60);
    }
    private JPanel analytics() {
        JPanel page=pagePanel();
        page.setLayout(new BorderLayout(0,12));
        JPanel filters=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        filters.setOpaque(false);
        JComboBox<String> skillFilter=new JComboBox<>();
        skillFilter.addItem("All skills");
        for(Skill skill:skills())skillFilter.addItem(skill.name);
        if(analyticsFocusSkill!=null) {
            skillFilter.setSelectedItem(analyticsFocusSkill);
            analyticsFocusSkill=null;
        }
        JComboBox<String> rangeFilter=new JComboBox<>(new String[]{"All time","Last 30 days","Last 90 days","Last year"});
        JComboBox<String> levelFilter=new JComboBox<>(new String[]{"All levels","Easy","Medium","Difficult"});
        filters.add(label("Skill",12,INK,true));filters.add(skillFilter);
        filters.add(label("Date range",12,INK,true));filters.add(rangeFilter);
        filters.add(label("Difficulty",12,INK,true));filters.add(levelFilter);
        page.add(filters,BorderLayout.NORTH);

        JPanel charts=new JPanel(new GridLayout(0,2,14,14));
        charts.setOpaque(false);
        Runnable refresh=()->{
            List<AssessmentRecord> records=assessmentRecords((String)skillFilter.getSelectedItem(),
                    (String)rangeFilter.getSelectedItem(),(String)levelFilter.getSelectedItem());
            charts.removeAll();
            List<Skill> filteredSkills=recordSkills(records);
            JPanel scores=chartCard(filteredSkills);
            scores.setPreferredSize(new Dimension(520,300));
            charts.add(scores);
            JPanel trend=card();trend.setLayout(new BorderLayout());
            trend.add(label("Performance over time · separate skill series",16,INK,true),BorderLayout.NORTH);
            trend.add(new AssessmentTrendChart(records),BorderLayout.CENTER);
            trend.setPreferredSize(new Dimension(520,300));charts.add(trend);
            JPanel radar=card();radar.setLayout(new BorderLayout());
            radar.add(label("Skill strengths · latest filtered scores",16,INK,true),BorderLayout.NORTH);
            radar.add(new RadarChart(filteredSkills),BorderLayout.CENTER);
            radar.setPreferredSize(new Dimension(520,300));charts.add(radar);
            JPanel distribution=card();distribution.setLayout(new BorderLayout());
            distribution.add(label("Proficiency distribution",16,INK,true),BorderLayout.NORTH);
            distribution.add(new ProficiencyChart(filteredSkills),BorderLayout.CENTER);
            distribution.setPreferredSize(new Dimension(520,300));charts.add(distribution);
            charts.add(attemptComparisonCard(records,(String)skillFilter.getSelectedItem()));
            charts.revalidate();charts.repaint();
        };
        skillFilter.addActionListener(e->refresh.run());
        rangeFilter.addActionListener(e->refresh.run());
        levelFilter.addActionListener(e->refresh.run());
        refresh.run();
        page.add(new JScrollPane(charts),BorderLayout.CENTER);
        return page;
    }

    private List<AssessmentRecord> assessmentRecords(String chosenSkill,String dateRange,String chosenLevel) {
        if(active.quizHistory==null)return List.of();
        LocalDate cutoff=switch(dateRange){
            case "Last 30 days"->LocalDate.now().minusDays(30);
            case "Last 90 days"->LocalDate.now().minusDays(90);
            case "Last year"->LocalDate.now().minusYears(1);
            default->null;
        };
        List<AssessmentRecord> records=new ArrayList<>();
        for(QuizSession session:active.quizHistory) {
            if(session.completedAt==null)continue;
            if(!"All skills".equals(chosenSkill)&&!session.skill.equals(chosenSkill))continue;
            if(cutoff!=null&&session.completedAt.toLocalDate().isBefore(cutoff))continue;
            List<AssessmentQuestionBank.Question> questions=new ArrayList<>();
            List<Integer> responses=new ArrayList<>();
            for(int i=0;i<session.questions.size();i++) {
                AssessmentQuestionBank.Question question=session.questions.get(i);
                if(!"All levels".equals(chosenLevel)&&!question.difficulty().equals(chosenLevel))continue;
                questions.add(question);responses.add(session.answers[i]);
            }
            if(questions.isEmpty())continue;
            int[] answers=responses.stream().mapToInt(Integer::intValue).toArray();
            AssessmentScorer.Result score=AssessmentScorer.calculate(questions,answers);
            records.add(new AssessmentRecord(session.skill,session.completedAt,session.attemptNumber,
                    (int)Math.round(score.percentage()),score.accuracy(),session.elapsedSeconds,
                    score.topicResults(),questions.size(),chosenLevel));
        }
        records.sort(Comparator.comparing((AssessmentRecord record)->record.at).reversed());
        return records;
    }

    private List<Skill> recordSkills(List<AssessmentRecord> records) {
        LinkedHashMap<String,Skill> byName=new LinkedHashMap<>();
        for(AssessmentRecord record:records) {
            Skill skill=byName.computeIfAbsent(record.skill,name->{
                return new Skill(name,categoryFor(name));
            });
            if(skill.history.isEmpty())skill.history.add(new Attempt(record.score,record.at,"Quiz score"));
        }
        return new ArrayList<>(byName.values());
    }

    private String categoryFor(String name) {
        for(int i=0;i<CATALOG.length;i++)for(String skill:CATALOG[i])if(skill.equals(name))return CATEGORIES[i];
        return "Skills";
    }

    private JPanel attemptComparisonCard(List<AssessmentRecord> records,String chosenSkill) {
        JPanel panel=card();panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(label("Compare two attempts",16,INK,true));panel.add(Box.createVerticalStrut(10));
        if("All skills".equals(chosenSkill)) {
            panel.add(label("Choose one skill in the filters above to compare its attempts.",13,MUTED,false));
            return panel;
        }
        List<AssessmentRecord> history=records.stream().filter(record->record.skill.equals(chosenSkill))
                .sorted(Comparator.comparing(record->record.at)).toList();
        if(history.size()<2) {
            panel.add(label("Complete two assessments within the selected filters to compare them.",13,MUTED,false));
            return panel;
        }
        AssessmentRecord previous=history.get(history.size()-2),latest=history.get(history.size()-1);
        panel.add(label(String.format(Locale.ROOT,"Previous %d%% → latest %d%% (%+.0f percentage points)",
                previous.score,latest.score,(double)(latest.score-previous.score)),15,BLUE,true));
        panel.add(label("Attempt "+previous.at.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))+" · Attempt "+
                latest.at.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),12,MUTED,false));
        panel.add(label(String.format(Locale.ROOT,"Accuracy %.0f%% → %.0f%% · time %s → %s",
                previous.accuracy,latest.accuracy,formatDuration(previous.seconds),formatDuration(latest.seconds)),13,MUTED,false));
        List<String> topicChanges=new ArrayList<>();
        for(String topic:latest.topics.keySet()) {
            int[] before=previous.topics.getOrDefault(topic,new int[]{0,0}),after=latest.topics.get(topic);
            double oldRate=before[1]==0?0:before[0]*100.0/before[1];
            double newRate=after[1]==0?0:after[0]*100.0/after[1];
            topicChanges.add(topic+" "+String.format(Locale.ROOT,"%+.0f",newRate-oldRate)+" pts");
        }
        if(!topicChanges.isEmpty())panel.add(label("Topic changes: "+String.join(" · ",topicChanges),12,INK,false));
        JScrollPane graph=new JScrollPane(new AttemptComparisonChart(previous,latest));
        graph.setBorder(BorderFactory.createLineBorder(new Color(232,236,244)));
        graph.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        graph.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        graph.getVerticalScrollBar().setUnitIncrement(24);
        graph.setPreferredSize(new Dimension(510,300));
        panel.add(Box.createVerticalStrut(8));
        panel.add(graph);
        return panel;
    }

    private class AttemptComparisonChart extends JPanel {
        private final AssessmentRecord previous,latest;
        private final List<String> topics;
        private final Color previousColor=new Color(151,169,211),latestColor=BLUE;
        AttemptComparisonChart(AssessmentRecord previous,AssessmentRecord latest) {
            this.previous=previous;
            this.latest=latest;
            Set<String> names=new TreeSet<>();
            names.addAll(previous.topics.keySet());
            names.addAll(latest.topics.keySet());
            topics=new ArrayList<>(names);
            setBackground(WHITE);
            setPreferredSize(new Dimension(490,Math.max(220,(topics.size()+1)*39+92)));
        }
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g=(Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int left=165,right=42,top=68,rowHeight=37,width=Math.max(1,getWidth()-left-right);
            g.setFont(new Font("Segoe UI",Font.PLAIN,10));
            for(int tick=0;tick<=5;tick++) {
                int x=left+width*tick/5;
                g.setColor(new Color(232,236,244));
                g.drawLine(x,top-8,x,top+(topics.size()+1)*rowHeight-5);
                g.setColor(MUTED);
                g.drawString((tick*20)+"%",x-10,top-14);
            }
            g.setFont(new Font("Segoe UI",Font.PLAIN,11));
            for(int index=0;index<=topics.size();index++) {
                boolean overall=index==0;
                String topic=overall?"Overall score":topics.get(index-1);
                int[] before=overall?null:previous.topics.get(topic);
                int[] after=overall?null:latest.topics.get(topic);
                double oldValue=overall?previous.score:topicPercent(before);
                double newValue=overall?latest.score:topicPercent(after);
                int y=top+index*rowHeight;
                g.setColor(INK);
                g.setFont(new Font("Segoe UI",overall?Font.BOLD:Font.PLAIN,11));
                g.drawString(clip(topic,24),4,y+14);
                drawComparisonBar(g,left,y,oldValue,previousColor,width);
                drawComparisonBar(g,left,y+15,newValue,latestColor,width);
                if(before==null&&!overall)g.setColor(MUTED);else g.setColor(INK);
                g.drawString(before==null&&!overall?"—":String.format(Locale.ROOT,"%.0f%%",oldValue),left+width+5,y+10);
                if(after==null&&!overall)g.setColor(MUTED);else g.setColor(INK);
                g.drawString(after==null&&!overall?"—":String.format(Locale.ROOT,"%.0f%%",newValue),left+width+5,y+25);
            }
            int legendY=getHeight()-14;
            g.setColor(previousColor);g.fillRoundRect(left,legendY-9,12,10,4,4);
            g.setColor(INK);g.drawString("Previous attempt",left+17,legendY);
            int latestLegendX=left+130;
            g.setColor(latestColor);g.fillRoundRect(latestLegendX,legendY-9,12,10,4,4);
            g.setColor(INK);g.drawString("Latest attempt",latestLegendX+17,legendY);
            g.dispose();
        }
        private double topicPercent(int[] counts) {
            return counts==null||counts[1]==0?0:counts[0]*100.0/counts[1];
        }
        private void drawComparisonBar(Graphics2D g,int left,int y,double value,Color color,int width) {
            int barWidth=(int)Math.round(width*Math.max(0,Math.min(100,value))/100.0);
            g.setColor(new Color(241,243,248));g.fillRoundRect(left,y,width,10,5,5);
            g.setColor(color);g.fillRoundRect(left,y,barWidth,10,5,5);
        }
        private String clip(String text,int max) {
            return text.length()>max?text.substring(0,max-1)+"…":text;
        }
    }

    private JPanel careers() {
        JPanel p=pagePanel();
        p.setLayout(new BorderLayout(0,14));
        p.add(label("Career matches are rule-based guidance estimates, not predictions or guarantees.",14,MUTED,false),BorderLayout.NORTH);
        JPanel list=new JPanel(new GridLayout(0,2,14,14));
        list.setOpaque(false);
        for(String career:CAREERS) {
            Map<String,Double> req=requirements(career);
            double match=match(req);
            JPanel c=card();
            c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
            c.add(label(career,18,INK,true));
            c.add(Box.createVerticalStrut(8));
            c.add(label(String.format(Locale.ROOT,"%.0f%% guidance match",match),21,BLUE,true));
            c.add(Box.createVerticalStrut(8));
            List<String> gaps=req.keySet().stream().filter(n-> {
                Skill s=findSkill(n); return s==null||s.history.isEmpty()||s.current()<req.get(n);
            }
            ).limit(3).toList();
            c.add(label(gaps.isEmpty()?"Your assessed skills meet the example targets.":"Good next skills: "+String.join(" · ",gaps),13,MUTED,false));
            JButton choose=button("Use as roadmap focus",new Color(238,241,255),BLUE);
            choose.addActionListener(e-> {
                active.career=career; syncRoadmap(career); save(); message(career+" selected for your roadmap.",GREEN); showPage("Learning Roadmap");
            }
            );
            c.add(Box.createVerticalGlue());
            c.add(choose);
            list.add(c);
        }
        p.add(new JScrollPane(list),BorderLayout.CENTER);
        return p;
    }
    private JPanel roadmap() {
        syncRoadmap(active.career);
        JPanel page=pagePanel();
        page.setLayout(new BorderLayout(0,12));
        page.add(label("Skill-by-skill learning paths · progress and completion are saved on this computer.",14,MUTED,false),BorderLayout.NORTH);
        JPanel list=new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        List<Skill> all=skills();
        if(active.focusSkill!=null)all.sort(Comparator.comparing(skill->!skill.name.equals(active.focusSkill)));
        long completed=active.roadmap.values().stream().filter(t->t.done).count();
        list.add(label("Your learning plans  ·  "+completed+" of "+active.roadmap.size()+" marked complete",16,INK,true));
        list.add(Box.createVerticalStrut(12));
        for(Skill skill:all) {
            RoadTask task=active.roadmap.computeIfAbsent(skill.name,n->new RoadTask(n));
            if(task.recommendedTopics==null)task.recommendedTopics=new ArrayList<>();
            JPanel card=card();
            card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
            String score=skill.history.isEmpty()?"Not assessed yet":String.format(Locale.ROOT,"Current %.0f%% (%s) · target %.0f%% · gap %.0f",skill.current(),skill.level(),skill.target,skill.gap());
            card.add(label(skill.name+"  ·  "+(skill.history.isEmpty()?"Assessment recommended":skill.gap()>0?"Improvement plan":"Target reached"),17,INK,true));
            card.add(label(score,12,MUTED,false));
            int stageNo=1;
            for(String stage:RoadmapCatalog.stages(skill.name)) {
                card.add(Box.createVerticalStrut(5));
                card.add(label("STAGE "+stageNo+"  ·  "+stage,12,BLUE,true));
                stageNo++;
            }
            card.add(Box.createVerticalStrut(5));
            card.add(label("PRACTICE  ·  "+RoadmapCatalog.practice(skill.name),12,INK,false));
            if(!task.recommendedTopics.isEmpty())card.add(label("FOCUS FROM YOUR LAST QUIZ  ·  "+String.join(" · ",task.recommendedTopics),12,RED,true));
            card.add(label("RESOURCE  ·  "+RoadmapCatalog.resource(skill.name),12,BLUE,false));
            JCheckBox done=new JCheckBox("Mark overall skill plan complete",task.done);
            done.setOpaque(false);
            done.setForeground(INK);
            done.addActionListener(e-> {
                task.done=done.isSelected(); save();
            }
            );
            card.add(done);
            list.add(card);
            list.add(Box.createVerticalStrut(10));
        }
        page.add(new JScrollPane(list),BorderLayout.CENTER);
        return page;
    }
    private JPanel cardMessage(String title,String text) {
        JPanel c=card();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.add(label(title,17,INK,true));
        c.add(Box.createVerticalStrut(8));
        c.add(label(text,13,MUTED,false));
        return c;
    }
    private JPanel history() {
        JPanel p=pagePanel();
        p.setLayout(new BorderLayout(0,12));
        JPanel filters=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        filters.setOpaque(false);
        JTextField search=field("Filter by skill");
        JComboBox<String> type=new JComboBox<>(new String[] {
            "All types","QUIZ","SELF"
        }
        );
        filters.add(search);
        filters.add(type);
        p.add(filters,BorderLayout.NORTH);
        String[] cols= {
            "Date","Skill","Category","Type","Score","Previous","Change","Explanation"
        }
        ;
        DefaultTableModel m=new DefaultTableModel(cols,0) {
            public boolean isCellEditable(int r,int c) {
                return false;
            }
        }
        ;
        List<Row> rows=historyRows();
        fillHistory(m,rows,"","All types");
        JTable t=new JTable(m);
        styleTable(t);
        Runnable filter=()->fillHistory(m,rows,search.getText(),(String)type.getSelectedItem());
        search.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filter.run();
            }
        }
        );
        type.addActionListener(e->filter.run());
        t.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if(event.getClickCount()!=2||t.getSelectedRow()<0)return;
                int row=t.convertRowIndexToModel(t.getSelectedRow());
                if(!"QUIZ".equals(m.getValueAt(row,3)))return;
                String skillName=String.valueOf(m.getValueAt(row,1));
                String date=String.valueOf(m.getValueAt(row,0));
                QuizSession session=(active.quizHistory==null?List.<QuizSession>of():active.quizHistory).stream().filter(item->item.skill.equals(skillName)
                        &&item.completedAt.format(DATE).equals(date)).findFirst().orElse(null);
                if(session!=null){active.lastQuiz=session;showPage("Assessment");}
            }
        });
        p.add(new JScrollPane(t),BorderLayout.CENTER);
        return p;
    }
    private void fillHistory(DefaultTableModel m,List<Row> rows,String q,String type) {
        m.setRowCount(0);
        String term=q.toLowerCase(Locale.ROOT);
        for(Row r:rows)if(r.skill.toLowerCase(Locale.ROOT).contains(term)&&(type.equals("All types")||r.type.equals(type)))m.addRow(new Object[] {
            r.at.format(DATE),r.skill,r.category,r.type,r.score+"%",r.previous==null?"—":r.previous+"%",r.previous==null?"—":String.format(Locale.ROOT,"%+.0f",r.score-r.previous),r.explanation
        }
        );
    }
    private List<Row> historyRows() {
        List<Row> out=new ArrayList<>();
        for(Skill s:skills()) {
            for(int i=0; i<s.history.size(); i++) {
                Attempt a=s.history.get(i);
                Double previous=i+1<s.history.size()?(double)s.history.get(i+1).score:null;
                String type=a.type==null?"SELF":a.type;
                out.add(new Row(a.at,s.name,s.category,type,a.score,previous,a.explanation));
            }
        }
        out.sort(Comparator.comparing((Row r)->r.at).reversed());
        return out;
    }
    private JPanel reports() {
        JPanel p=pagePanel();
        p.setLayout(new GridBagLayout());
        JPanel c=card();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setPreferredSize(new Dimension(620,390));
        c.add(label("Your personal skill report",22,INK,true));
        c.add(Box.createVerticalStrut(8));
        c.add(label("Preview your profile and export it for your portfolio or advisor.",14,MUTED,false));
        c.add(Box.createVerticalStrut(18));
        JTextArea preview=new JTextArea(reportText());
        preview.setEditable(false);
        preview.setFont(new Font("Consolas",0,12));
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        c.add(new JScrollPane(preview));
        c.add(Box.createVerticalStrut(14));
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);
        JButton csv=button("Export CSV",BLUE,WHITE),pdf=button("Export PDF",NAVY,WHITE);
        csv.addActionListener(e->export(false));
        pdf.addActionListener(e->export(true));
        buttons.add(csv);
        buttons.add(pdf);
        c.add(buttons);
        p.add(c);
        return p;
    }
    private JPanel profile() {
        JPanel p=pagePanel();
        p.setLayout(new GridBagLayout());
        JPanel c=card();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setPreferredSize(new Dimension(680,560));
        JTextField name=field("Full name"),college=field("College or university"),course=field("Course / department");
        name.setText(active.name);
        college.setText(active.college);
        course.setText(active.course);
        c.add(label("Your profile",24,INK,true));
        c.add(Box.createVerticalStrut(6));
        c.add(label("Keep your student details up to date.",13,MUTED,false));
        c.add(Box.createVerticalStrut(22));
        addField(c,"Full name",name);
        JTextField email=field("Email address");
        email.setText(active.email);
        email.setEditable(false);
        email.setBackground(new Color(245,247,251));
        addField(c,"Email (account identifier)",email);
        addField(c,"College / University",college);
        addField(c,"Course / Department",course);
        JComboBox<String> year=new JComboBox<>(yearOptions());
        year.setSelectedIndex(Math.max(0,Math.min(7,active.year-1)));
        year.setMaximumSize(new Dimension(190,42));
        year.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(label("Study year",12,INK,true));
        c.add(Box.createVerticalStrut(5));
        c.add(year);
        JButton saveBtn=button("Save profile",BLUE,WHITE);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(190,46));
        saveBtn.addActionListener(e-> {
            if(name.getText().trim().length()<2) {
                message("Enter a valid name.",RED); return;
            }
            active.name=name.getText().trim(); active.college=college.getText().trim(); active.course=course.getText().trim(); active.year=year.getSelectedIndex()+1; save();
            ((JLabel)sidebar.getComponent(2)).setText(active.name);
            message("Profile updated.",GREEN);
            showPage("My Profile");
        }
        );
        c.add(Box.createVerticalStrut(16));
        c.add(saveBtn);
        p.add(c);
        return p;
    }
    private String[] yearOptions() {
        return new String[]{"Year 1","Year 2","Year 3","Year 4","Year 5","Year 6","Year 7","Year 8"};
    }
    private JPanel addLabelled(String s,JComponent j) {
        JPanel p=new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.add(label(s,12,INK,true));
        p.add(Box.createVerticalStrut(5));
        j.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        p.add(j);
        p.add(Box.createVerticalStrut(10));
        return p;
    }
    private JPanel settings() {
        JPanel p=pagePanel();
        p.setLayout(new GridBagLayout());
        JPanel c=card();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setPreferredSize(new Dimension(560,400));
        c.add(label("Settings & privacy",22,INK,true));
        c.add(Box.createVerticalStrut(16));
        c.add(label("Your account and assessments are stored locally on this computer.",14,MUTED,false));
        c.add(label("Data file: "+STORE,12,MUTED,false));
        c.add(Box.createVerticalStrut(20));
        JButton export=button("Back up my data",BLUE,WHITE);
        export.addActionListener(e-> {
            JFileChooser fc=new JFileChooser(); fc.setSelectedFile(new File("skillsight-backup.data")); if(fc.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION)try {
                Files.copy(STORE,fc.getSelectedFile().toPath(),StandardCopyOption.REPLACE_EXISTING); message("Backup saved.",GREEN);
            }
            catch(Exception ex) {
                message("Could not create backup.",RED);
            }
        }
        );
        JButton restore=button("Restore from backup",new Color(238,241,255),BLUE);
        restore.addActionListener(e-> {
            JFileChooser fc=new JFileChooser(); if(fc.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION)try {
                if(JOptionPane.showConfirmDialog(frame,"Replace the current local data with this backup?","Restore backup",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                    data=(Data)new ObjectInputStream(Files.newInputStream(fc.getSelectedFile().toPath())).readObject(); active=data.accounts.get(active.email); if(active.quizHistory==null)active.quizHistory=new ArrayList<>(); save(); showApp();
                }
            }
            catch(Exception ex) {
                message("Could not restore this backup file.",RED);
            }
        }
        );
        c.add(export);
        c.add(Box.createVerticalStrut(10));
        c.add(restore);
        p.add(c);
        return p;
    }
    private void syncRoadmap(String career) {
        Map<String,Double> req=career==null?Map.of():requirements(career);
        for(Skill s:skills())if(!s.history.isEmpty()&&s.gap()>0&&(req.isEmpty()||req.containsKey(s.name))) {
            RoadTask t=active.roadmap.computeIfAbsent(s.name,n->new RoadTask(n));
            t.priority=(int)Math.ceil(s.gap()*(req.isEmpty()?1:req.get(s.name)/100));
        }
        active.roadmap.entrySet().removeIf(e-> {
            Skill s=findSkill(e.getKey()); return s==null||s.gap()<=0;
        }
        );
        save();
    }
    private Map<String,Double> requirements(String career) {
        LinkedHashMap<String,Double> m=new LinkedHashMap<>();
        if(career.contains("Java"))m.put("Java",100d);
        if(career.contains("Backend")||career.contains("Java")||career.contains("Data Engineer"))m.put("Database Management Systems",80d);
        if(career.contains("Full-Stack"))m.put("Web Development",100d);
        if(career.contains("Data"))m.put("Data Analysis",100d);
        if(career.contains("Tester"))m.put("Software Development",70d);
        if(career.contains("Cybersecurity"))m.put("Problem Solving",80d);
        m.put("Problem Solving",Math.max(m.getOrDefault("Problem Solving",0d),70d));
        m.put("Communication",40d);
        return m;
    }
    private double match(Map<String,Double> req) {
        double total=0,earned=0;
        for(var e:req.entrySet()) {
            total+=e.getValue();
            Skill s=findSkill(e.getKey());
            earned+=e.getValue()*(s==null||s.history.isEmpty()?0:Math.min(1,s.current()/100));
        }
        return total==0?0:earned/total*100;
    }
    private List<Skill> skills() {
        List<Skill> result=new ArrayList<>();
        for(int i=0; i<CATALOG.length; i++) {
            String category=CATEGORIES[i];
            for(String n:CATALOG[i])result.add(active.skills.computeIfAbsent(n,k->new Skill(k,category)));
        }
        return result;
    }
    private Skill findSkill(String name) {
        if(name==null)return null;
        return skills().stream().filter(s->s.name.equals(name)).findFirst().orElse(null);
    }
    private String reportText() {
        StringBuilder b=new StringBuilder("SKILLSIGHT — STUDENT SKILL REPORT\n");
        b.append("Student: ").append(active.name).append("\nEmail: ").append(active.email).append("\nGenerated: ").append(LocalDate.now()).append("\n\nScores are self-reported; they are not independently verified.\n\nSKILL SUMMARY\n");
        for(Skill s:skills())if(!s.history.isEmpty())b.append(String.format(Locale.ROOT,"%-36s %5.0f%%  %-14s target %3.0f%%  gap %3.0f%n",s.name,s.current(),s.level(),s.target,s.gap()));
        b.append("\nCAREER GUIDANCE\n");
        for(String c:CAREERS)b.append(String.format(Locale.ROOT,"%-26s %5.0f%%%n",c,match(requirements(c))));
        b.append("\nROADMAP\n");
        for(RoadTask t:active.roadmap.values())b.append((t.done?"[x] ":"[ ] ")+t.skill+" · priority "+t.priority+"\n");
        return b.toString();
    }
    private void export(boolean pdf) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("skillsight-report." + (pdf ? "pdf" : "csv")));
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        try {
            Path destination = chooser.getSelectedFile().toPath();
            if (pdf) {
                ReportExporter.writePdf(destination, reportText());
            }
            else {
                List<String[]> rows = new ArrayList<>();
                for (Skill skill : skills()) {
                    if (skill.history.isEmpty()) continue;
                    rows.add(new String[] {
                        skill.name, skill.category,
                        String.valueOf(skill.current()), skill.level(),
                        String.valueOf(skill.target), String.valueOf(skill.gap()),
                        skill.history.get(0).at.toString()
                    }
                    );
                }
                ReportExporter.writeCsv(destination, rows);
            }
            message("Report exported successfully.", GREEN);
        }
        catch (Exception error) {
            message("Export failed: " + error.getMessage(), RED);
        }
    }
    private static void checkScore(double x) {
        if(x<0||x>100||Double.isNaN(x))throw new IllegalArgumentException("Score must be between 0 and 100.");
    }
    private static String level(double x) {
        if(x<=20)return"Beginner";
        if(x<=40)return"Basic";
        if(x<=60)return"Intermediate";
        if(x<=80)return"Proficient";
        return"Advanced";
    }
    private static String first(String s) {
        int i=s.indexOf(' ');
        return i<0?s:s.substring(0,i);
    }
    private JLabel label(String s,int size,Color color,boolean bold) {
        JLabel l=new JLabel(s);
        l.setFont(new Font("Segoe UI",bold?Font.BOLD:Font.PLAIN,size));
        l.setForeground(color);
        return l;
    }
    private JButton button(String s,Color bg,Color fg) {
        JButton b=new JButton(s);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12,16,12,16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setRolloverEnabled(true);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(b.isEnabled())b.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
            }
        }
        );
        return b;
    }
    private JButton link(String s) {
        JButton b=new JButton(s);
        b.setForeground(BLUE);
        b.setBackground(WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private void styleTable(JTable t) {
        t.setRowHeight(34);
        t.setFont(new Font("Segoe UI",0,13));
        t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        t.getTableHeader().setBackground(new Color(239,242,249));
        t.setSelectionBackground(new Color(227,232,255));
        t.setGridColor(new Color(236,239,245));
    }
    private void message(String s,Color c) {
        if(toast!=null&&frame!=null&&frame.isVisible()) {
            toast.setText("  "+s);
            toast.setForeground(c);
        }
        else if(frame!=null)JOptionPane.showMessageDialog(frame,s,"SkillSight",JOptionPane.INFORMATION_MESSAGE);
    }
    private class SkillChart extends JPanel implements Scrollable {
        List<Skill> rows;
        SkillChart(List<Skill> r) {
            rows=r;
            setBackground(WHITE);
            int assessedCount=(int)r.stream().filter(s->!s.history.isEmpty()).count();
            setPreferredSize(new Dimension(520,Math.max(240,assessedCount*34+70)));
        }
        public Dimension getPreferredScrollableViewportSize() { return new Dimension(520,420); }
        public int getScrollableUnitIncrement(Rectangle visibleRect,int orientation,int direction) {
            return orientation==SwingConstants.VERTICAL?34:24;
        }
        public int getScrollableBlockIncrement(Rectangle visibleRect,int orientation,int direction) {
            return orientation==SwingConstants.VERTICAL?Math.max(34,visibleRect.height-34):Math.max(24,visibleRect.width-24);
        }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() { return false; }
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            Graphics2D g=(Graphics2D)gg.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int left=110,right=25,top=35,bottom=25,w=getWidth()-left-right;
            g.setFont(new Font("Segoe UI",0,11));
            for(int i=0; i<=5; i++) {
                int x=left+w*i/5;
                g.setColor(new Color(232,236,244));
                g.drawLine(x,top,x,getHeight()-bottom);
                g.setColor(MUTED);
                g.drawString((i*20)+"",x-7,getHeight()-5);
            }
            List<Skill> vals=rows.stream().filter(s->!s.history.isEmpty()).toList();
            if(vals.isEmpty()) {
                g.setColor(MUTED);
                g.drawString("Your first scores will appear here.",left+18,top+40);
            }
            for(int i=0; i<vals.size(); i++) {
                Skill s=vals.get(i);
                int y=top+20+i*34;
                g.setColor(INK);
                String nm=s.name.length()>16?s.name.substring(0,15)+"…":s.name;
                g.drawString(nm,4,y+12);
                int bw=(int)(w*s.current()/100);
                g.setColor(new Color(232,236,255));
                g.fillRoundRect(left,y,w,17,8,8);
                g.setColor(BLUE);
                g.fillRoundRect(left,y,bw,17,8,8);
                g.setColor(INK);
                g.drawString(String.format(Locale.ROOT,"%.0f",s.current()),left+bw+5,y+13);
            }
            g.dispose();
        }
    }
    private class HistoryChart extends JPanel {
        List<Skill> rows;
        HistoryChart(List<Skill> r) {
            rows=r;
            setBackground(WHITE);
        }
        protected void paintComponent(Graphics gg) {
            super.paintComponent(gg);
            Graphics2D g=(Graphics2D)gg.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            List<Attempt> a=rows.stream().flatMap(s->s.history.stream()).sorted(Comparator.comparing(x->x.at)).toList();
            int l=42,r=20,t=35,b=35,w=getWidth()-l-r,h=getHeight()-t-b;
            g.setColor(MUTED);
            for(int i=0; i<=5; i++) {
                int y=t+h*i/5;
                g.setColor(new Color(234,237,244));
                g.drawLine(l,y,l+w,y);
                g.setColor(MUTED);
                g.drawString((100-i*20)+"",5,y+4);
            }
            if(a.size()<2) {
                g.drawString("Complete another assessment to see progress over time.",l+10,t+25);
            }
            else {
                g.setColor(BLUE);
                g.setStroke(new BasicStroke(3));
                for(int i=1; i<a.size(); i++) {
                    int x1=l+w*(i-1)/(a.size()-1),x2=l+w*i/(a.size()-1),y1=t+h-(int)(h*a.get(i-1).score/100),y2=t+h-(int)(h*a.get(i).score/100);
                    g.drawLine(x1,y1,x2,y2);
                    g.fillOval(x2-4,y2-4,8,8);
                }
            }
            g.dispose();
        }
    }
    private class RadarChart extends JPanel {
        private final List<Skill> assessed;
        RadarChart(List<Skill> skills) {
            assessed=skills.stream().filter(skill->!skill.history.isEmpty()).limit(8).toList();
            setBackground(WHITE);
            setPreferredSize(new Dimension(380,250));
        }
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g=(Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if(assessed.isEmpty()) {
                g.setColor(MUTED);g.drawString("Complete an assessment to see skill strengths.",18,35);g.dispose();return;
            }
            int cx=getWidth()/2,cy=getHeight()/2+4,radius=Math.max(35,Math.min(getWidth(),getHeight())/3);
            int count=assessed.size();
            g.setColor(new Color(231,235,245));
            for(int ring=1;ring<=4;ring++) {
                int r=radius*ring/4;
                for(int i=0;i<count;i++) {
                    double a=-Math.PI/2+2*Math.PI*i/count;
                    int x=cx+(int)(r*Math.cos(a)),y=cy+(int)(r*Math.sin(a));
                    double next=-Math.PI/2+2*Math.PI*(i+1)/count;
                    g.drawLine(x,y,cx+(int)(r*Math.cos(next)),cy+(int)(r*Math.sin(next)));
                }
            }
            Polygon area=new Polygon();
            for(int i=0;i<count;i++) {
                double a=-Math.PI/2+2*Math.PI*i/count;
                double value=Math.max(0,Math.min(100,assessed.get(i).current()))/100.0;
                int x=cx+(int)(radius*value*Math.cos(a)),y=cy+(int)(radius*value*Math.sin(a));
                area.addPoint(x,y);
                g.setColor(INK);
                String name=assessed.get(i).name;
                if(name.length()>14)name=name.substring(0,13)+"…";
                g.drawString(name,cx+(int)((radius+20)*Math.cos(a))-20,cy+(int)((radius+20)*Math.sin(a))+4);
            }
            g.setColor(new Color(79,99,239,55));g.fillPolygon(area);
            g.setColor(BLUE);g.setStroke(new BasicStroke(2));g.drawPolygon(area);
            g.dispose();
        }
    }
    private class ScoreRing extends JPanel {
        private final int score;
        ScoreRing(int score) {
            this.score=Math.max(0,Math.min(100,score));
            setOpaque(false);
            setPreferredSize(new Dimension(142,142));
        }
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g=(Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int size=Math.min(getWidth(),getHeight())-18;
            int x=(getWidth()-size)/2,y=(getHeight()-size)/2;
            g.setStroke(new BasicStroke(11,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.setColor(new Color(232,236,247));g.drawArc(x,y,size,size,90,-360);
            g.setColor(BLUE);g.drawArc(x,y,size,size,90,-(int)Math.round(360*score/100.0));
            g.setColor(INK);g.setFont(new Font("Segoe UI",Font.BOLD,24));
            String value=score+"%";FontMetrics metrics=g.getFontMetrics();
            g.drawString(value,(getWidth()-metrics.stringWidth(value))/2,getHeight()/2+7);
            g.dispose();
        }
    }
    private class ProficiencyChart extends JPanel {
        private final List<Skill> assessed;
        private final Color[] colors={new Color(220,91,105),new Color(238,174,62),new Color(147,112,210),new Color(68,139,214),new Color(24,145,105)};
        private final String[] names={"Beginner 0–20","Basic 21–40","Intermediate 41–60","Proficient 61–80","Advanced 81–100"};
        ProficiencyChart(List<Skill> skills) {
            assessed=skills.stream().filter(skill->!skill.history.isEmpty()).toList();
            setBackground(WHITE);
            setPreferredSize(new Dimension(420,250));
        }
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g=(Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if(assessed.isEmpty()) {
                g.setColor(MUTED);g.drawString("No assessed skills to group yet.",18,35);g.dispose();return;
            }
            int[] counts=new int[5];
            for(Skill skill:assessed) {
                double score=skill.current();
                counts[score<=20?0:score<=40?1:score<=60?2:score<=80?3:4]++;
            }
            int total=assessed.size(),start=90,cx=100,cy=getHeight()/2,r=65;
            for(int i=0;i<counts.length;i++) {
                int extent=(int)Math.round(360.0*counts[i]/total);
                g.setColor(colors[i]);g.fillArc(cx-r,cy-r,r*2,r*2,start,extent);start+=extent;
                int y=cy-27+i*24;
                g.fillRoundRect(200,y-10,12,12,4,4);
                g.setColor(INK);g.drawString(names[i]+" · "+counts[i],220,y);
            }
            g.setColor(MUTED);g.drawString("Based on latest saved score per skill",25,getHeight()-12);
            g.dispose();
        }
    }
    private class AssessmentTrendChart extends JPanel {
        private final List<AssessmentRecord> records;
        private final Color[] palette={BLUE,new Color(24,145,105),new Color(211,125,44),new Color(147,112,210),new Color(220,91,105),new Color(55,145,170)};
        AssessmentTrendChart(List<AssessmentRecord> records) {
            this.records=records;
            setBackground(WHITE);
            setPreferredSize(new Dimension(520,260));
        }
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g=(Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if(records.isEmpty()) {
                g.setColor(MUTED);g.drawString("No assessment results in this filter.",18,35);g.dispose();return;
            }
            int left=42,right=20,top=24,bottom=84,width=Math.max(1,getWidth()-left-right),height=Math.max(1,getHeight()-top-bottom);
            for(int tick=0;tick<=5;tick++) {
                int y=top+height*tick/5;
                g.setColor(new Color(232,236,244));g.drawLine(left,y,left+width,y);
                g.setColor(MUTED);g.drawString(String.valueOf(100-tick*20),5,y+4);
            }
            LocalDateTime first=records.stream().map(record->record.at).min(LocalDateTime::compareTo).orElseThrow();
            LocalDateTime last=records.stream().map(record->record.at).max(LocalDateTime::compareTo).orElseThrow();
            long span=Math.max(1,Duration.between(first,last).toSeconds());
            List<String> series=records.stream().map(record->record.skill).distinct().sorted().toList();
            int colorIndex=0;
            for(String skill:series) {
                final int color=colorIndex++;
                Color seriesColor=Color.getHSBColor(color/(float)Math.max(1,series.size()),0.72f,0.76f);
                List<AssessmentRecord> points=records.stream().filter(record->record.skill.equals(skill))
                        .sorted(Comparator.comparing(record->record.at)).toList();
                g.setColor(seriesColor);g.setStroke(new BasicStroke(2));
                for(int i=0;i<points.size();i++) {
                    AssessmentRecord point=points.get(i);
                    int x=left+(int)(width*Duration.between(first,point.at).toSeconds()/(double)span);
                    int y=top+height-(int)(height*point.score/100.0);
                    if(i>0) {
                        AssessmentRecord previous=points.get(i-1);
                        int px=left+(int)(width*Duration.between(first,previous.at).toSeconds()/(double)span);
                        int py=top+height-(int)(height*previous.score/100.0);
                        g.drawLine(px,py,x,y);
                    }
                    g.fillOval(x-4,y-4,8,8);
                }
                int legendX=left+(color%6)*(width/6),legendY=getHeight()-48+(color/6)*13;
                g.setColor(seriesColor);g.fillOval(legendX,legendY-8,8,8);
                g.setColor(INK);String title=skill.length()>16?skill.substring(0,15)+"…":skill;
                g.drawString(title,legendX+12,legendY);
            }
            g.setColor(MUTED);
            g.drawString(first.format(DateTimeFormatter.ofPattern("dd MMM yy")),left,getHeight()-74);
            String end=last.format(DateTimeFormatter.ofPattern("dd MMM yy"));
            g.drawString(end,left+width-g.getFontMetrics().stringWidth(end),getHeight()-74);
            g.dispose();
        }
    }
    private static class Data implements Serializable {
        Map<String,Account> accounts=new HashMap<>();
        private static final long serialVersionUID=1L;
    }
    private static class Account implements Serializable {
        private static final long serialVersionUID=1L;
        String name,email,hash,salt;
        String college="",course="",career;
        int year=1;
        Map<String,Skill> skills=new HashMap<>();
        Map<String,RoadTask> roadmap=new HashMap<>();
        List<QuizSession> quizHistory=new ArrayList<>();
        QuizSession pendingQuiz,lastQuiz;
        String focusSkill;
        Account(String n,String e,String salted)throws Exception {
            name=n;
            email=e;
            String[] p=salted.split(":");
            salt=p[0];
            hash=p[1];
        }
    }
    private static class Skill implements Serializable {
        private static final long serialVersionUID=1L;
        String name,category;
        double target=70;
        List<Attempt> history=new ArrayList<>();
        Skill(String n,String c) {
            name=n;
            category=c;
        }
        double current() {
            return history.isEmpty()?0:history.get(0).score;
        }
        double gap() {
            return Math.max(0,target-current());
        }
        String level() {
            return SkillSight.level(current());
        }
    }
    private static class Attempt implements Serializable {
        private static final long serialVersionUID=1L;
        int score;
        LocalDateTime at;
        String explanation;
        String type="SELF";
        Attempt(int s,LocalDateTime a,String e) {
            score=s;
            at=a;
            explanation=e;
        }
    }
    private static class QuizSession implements Serializable {
        private static final long serialVersionUID=1L;
        String skill;
        List<AssessmentQuestionBank.Question> questions;
        int[] answers;
        int currentIndex;
        LocalDateTime startedAt,completedAt;
        int durationSeconds=1200,score,correct,incorrect,unanswered,attempted,elapsedSeconds,marksObtained,totalMarks;
        int attemptNumber=1;
        int previousScore=-1;
        double accuracy;
        Map<String,int[]> topicResults=new LinkedHashMap<>();
        Map<String,int[]> levelResults=new LinkedHashMap<>();
        QuizSession(String skill,List<AssessmentQuestionBank.Question> questions) {
            this.skill=skill;
            this.questions=questions;
            this.answers=new int[questions.size()];
            Arrays.fill(answers,-1);
            this.startedAt=LocalDateTime.now();
        }
    }
    private static class RoadTask implements Serializable {
        private static final long serialVersionUID=1L;
        String skill;
        int priority;
        boolean done;
        List<String> recommendedTopics=new ArrayList<>();
        RoadTask(String s) {
            skill=s;
        }
    }
    private record Row(LocalDateTime at,String skill,String category,String type,int score,Double previous,String explanation) {
    }
    private record AssessmentRecord(String skill,LocalDateTime at,int attemptNumber,int score,
            double accuracy,int seconds,Map<String,int[]> topics,int questionCount,String difficulty) {
    }
    private long attempts() {
        return skills().stream().mapToLong(s->s.history.size()).sum();
    }
}