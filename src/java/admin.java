
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class admin implements ActionListener {

    JFrame f;
    JPanel p1;
    JPanel p2;
    JTree tree;
    Statement stmt;
    Connection con;
    String s = "";
    JSplitPane jsp;
    Toolkit tk;

    JLabel l1;
    JLabel l2;
    JLabel l3;
    JLabel l4;
    JLabel l5;
    JLabel l6;
    JLabel l7;

    JTextField jt1, jt2, jt3, jt4, jt5, jt6;
    JButton b1, b2, b3;
    JComboBox<String> jc1;
    JMenuItem edit, remove;
    JPopupMenu menu;

    static void buildtree() {

    }

    public admin() {

        f = new JFrame("ADMIN_PANEL");
        p1 = new JPanel();
        p2 = new JPanel();

        p1.setLayout(null);
        p2.setLayout(new FlowLayout());

        l1 = new JLabel("Candidate_Id:");
        l2 = new JLabel("First_Name:");
        l3 = new JLabel("Last_Name:");
        l4 = new JLabel("Ward No.:");
        l5 = new JLabel("Party:");
        l6 = new JLabel("Information:");
        l7 = new JLabel("Enter Details");

        jt1 = new JTextField(30);
        jt2 = new JTextField(30);
        jt3 = new JTextField(30);
        jt4 = new JTextField(30);
        jt5 = new JTextField(30);
        jt6 = new JTextField(30);

        b1 = new JButton("Save Changes");
        b2 = new JButton("Add Candidate");
        b3 = new JButton("Add");
        b2.setBounds(65, 510, 160, 40);
        
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        p1.add(b2);

        menu = new JPopupMenu();
        edit = new JMenuItem("Edit");
        remove = new JMenuItem("Remove");
        edit.addActionListener(this);
        remove.addActionListener(this);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting Sizes
        tk = Toolkit.getDefaultToolkit();
        int x = ((int) tk.getScreenSize().getWidth());
        int y = ((int) tk.getScreenSize().getHeight());
        f.setSize(x, y);

        int p1h = (int) (Math.round(y * 0.30));
        int p1w = (int) (Math.round(x * 0.30));
        System.out.println(p1h);
        p1.setSize(new Dimension(p1w, 1200));
        p1.setLocation(0, 0);

        int p2h = (int) (Math.round(y * 0.70));
        int p2w = (int) (Math.round(x * 0.70));
        p2.setSize(new Dimension(p2w, p2h));
        p2.setLocation(410, 0);

        System.out.println("sdf");

        try {

            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connected to:");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/e-voting", "root", "tanha");
            System.out.println("Connected to database:");
            stmt = con.createStatement();

            String sql = "select distinct  wardno from cand_details";
            ResultSet rs = stmt.executeQuery(sql);

            DefaultMutableTreeNode ward[] = new DefaultMutableTreeNode[10];
            DefaultMutableTreeNode t1 = new DefaultMutableTreeNode("Ward-No");
            int i = 0;

            // fetching ward no's:
            while (rs.next()) {
                ward[i] = new DefaultMutableTreeNode(rs.getString(1));
                t1.add(ward[i]);
                s += rs.getString(1);
                i++;
            }

            // fetching candidates of each ward
            for (int j = 0; j < i; j++) {
                String wno = ward[j].toString();
                rs = stmt.executeQuery("select cid,fname,sname,party from cand_details where wardno = '" + wno + "' ");
                while (rs.next()) {
                    String fname = rs.getString(2);
                    String sname = rs.getString(3);
                    String cid = rs.getString(1);
                    String p=rs.getString(4);
                    String name = "[" + cid + "]. " + fname + " " + sname;
                    Party par = null;
                    if("BJP".equals(p))
                    {
                        
                        System.out.println("tanha");
                        par=new Party(name,"/bjp.jpe"); 
                    }
                    else if("Congress".equals(p))
                    {
                        par=new Party(name,"/congress.png");
                    }
                    
                    DefaultMutableTreeNode t2 = new DefaultMutableTreeNode(par);
                    ward[j].add(t2);
                }
            }

            System.out.println(s);
            tree = new JTree(t1);
            tree.setCellRenderer(new PartyTreeCellRenderer());
            tree.setBounds(0, 0, 500, 500);

            //handling right clicks (pop up)
            tree.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent me) {
                    if (SwingUtilities.isRightMouseButton(me)) {
                        TreePath path = tree.getPathForLocation(me.getX(), me.getY());
                        Rectangle pathBounds = tree.getUI().getPathBounds(tree, path);
                        DefaultMutableTreeNode n=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                        if (n.getLevel()==2) {
                            menu.add(edit);
                            menu.add(remove);
                            menu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);

                        }

                    }
                }
            });

            tree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {

                    TreePath path = e.getPath();
                    if (path.getPathCount() == 3) {
                        String cid = "";
                        String fname = "";
                        String sname = "";
                        String wardno = "";
                        String party = "";
                        String info = "";
                        String ci = path.getPathComponent(path.getPathCount() - 1).toString();
                        String cidd = "";
                        for (int k = 0; k < ci.length(); k++) {
                            if (ci.charAt(k) == ']') {
                                break;
                            }
                            cidd = ci.charAt(k) + "";
                        }
                        System.out.println(cidd);
                        try {
                            ResultSet rs = stmt.executeQuery("select * from cand_details where cid = '" + cidd + "' ");
                            while (rs.next()) {
                                cid = rs.getString(1);
                                fname = rs.getString(2);
                                sname = rs.getString(3);
                                wardno = rs.getString(4);
                                party = rs.getString(5);
                                info = rs.getString(6);
                            }

                            System.out.println(sname);

                            jt1.setText(cid);
                            jt2.setText(fname);
                            jt3.setText(sname);
                            jt4.setText(wardno);
                            jt5.setText(party);
                            jt6.setText(info);
                            
                            jt1.setEditable(false);
                            jt2.setEditable(false);
                            jt3.setEditable(false);
                            jt4.setEditable(false);
                            jt5.setEditable(false);
                            jt6.setEditable(false);                            
                            
                            l1.setBounds(0, 0, 200, 50);
                            jt1.setBounds(300, 0, 200, 50);
                            l2.setBounds(0, 100, 200, 50);
                            jt2.setBounds(300, 100, 200, 50);
                            l3.setBounds(0, 200, 200, 50);
                            jt3.setBounds(300, 200, 200, 50);
                            l4.setBounds(0, 300, 200, 50);
                            jt4.setBounds(300, 300, 200, 50);
                            l5.setBounds(0, 400, 200, 50);
                            jt5.setBounds(300, 400, 200, 50);
                            l6.setBounds(0, 500, 200, 50);
                            jt6.setBounds(300, 500, 200, 50);

                            p2.add(l1);
                            p2.add(jt1);
                            p2.add(l2);
                            p2.add(jt2);
                            p2.add(l3);
                            p2.add(jt3);
                            p2.add(l4);
                            p2.add(jt4);
                            p2.add(l5);
                            p2.add(jt5);
                            p2.add(l6);
                            p2.add(jt6);
                            p2.add(b1);

                            f.setVisible(true);

                        } catch (Exception ex) {

                        }

                    }
                }
            });

            p1.add(tree);

            jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            jsp.setResizeWeight(0.3);
            jsp.setEnabled(false);
            jsp.add(p1);
            jsp.add(p2);
            f.add(jsp);
            f.setVisible(true);

        } catch (Exception e) {
            System.out.println("SQL");
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Save Changes") {
            String id = jt1.getText();
            String fname = jt2.getText();
            String sname = jt3.getText();
            String wardno = jt4.getText();
            String party = jt5.getText();
            String info = jt6.getText();

            try {
                stmt.executeUpdate("update cand_details  set fname='" + fname + "', sname='" + sname + "', wardno='" + wardno + "', party = '" + party + "' , info = '" + info + "' where cid='" + id + "'");
                System.out.println("Records Updated");

            } catch (SQLException ex) {
                Logger.getLogger(admin.class.getName()).log(Level.SEVERE, null, ex);

            }
            JOptionPane.showMessageDialog(null, "Records Updated", "", JOptionPane.INFORMATION_MESSAGE);
            f.setVisible(false);
            new admin();
        }
        if (e.getActionCommand() == "Add Candidate") {
            p2.setVisible(false);
            p2.add(l7);
            p2.add(l1);
            p2.add(jt1);
            p2.add(l2);
            p2.add(jt2);
            p2.add(l3);
            p2.add(jt3);
            p2.add(l4);
            p2.add(jt4);
            p2.add(l5);
            p2.add(jt5);
            p2.add(l6);
            p2.add(jt6);
            p2.add(b3);

            p2.setVisible(true);
        }
        if (e.getActionCommand() == "Add") {
            String cid = jt1.getText();
            String fname = jt2.getText();
            String sname = jt3.getText();
            String wardno = jt4.getText();
            String party = jt5.getText();
            String info = jt6.getText();

            try {
                ResultSet rs = null;
                rs.moveToInsertRow();
                stmt.executeQuery("insert into cand_details values('" + cid + "', '" + fname + "','" + sname + "','" + wardno + "', '" + party + "', '" + info + "')");
                rs.moveToCurrentRow();
                JOptionPane.showMessageDialog(f, "Value Inserted", "", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                Logger.getLogger(admin.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (e.getActionCommand() == "Edit") {
            jt1.setEditable(true);
            jt2.setEditable(true);
            jt3.setEditable(true);
            jt4.setEditable(true);
            jt5.setEditable(true);
            jt6.setEditable(true);
        }
        if (e.getActionCommand() == "Remove") {

        }

    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("sf");
                new admin();

            }

        });
    }

}
 class PartyTreeCellRenderer implements TreeCellRenderer {
        private JLabel label;

        PartyTreeCellRenderer() {
            label = new JLabel();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof Party) {
                Party p = (Party) o;
                URL imageUrl = getClass().getResource(p.getPartyIcon());
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(p.getName());
            } else {
                label.setIcon(null);
                label.setText("" + value);
            }
            return label;
        }
    }

    class Party {
        private String name;
        private String PartyIcon;

        Party(String name, String partyIcon) {
            this.name = name;
            this.PartyIcon = partyIcon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPartyIcon() {
            return PartyIcon;
        }

        public void setPartyIcon(String flagIcon) {
            this.PartyIcon = flagIcon;
        }
    }


