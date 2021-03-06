/* 
 * Copyright (c) 2015 Sebastian Brudzinski
 * 
 * See the file LICENSE for copying permission.
 */
package latexstudio.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import latexstudio.editor.remote.DbxState;
import latexstudio.editor.util.ApplicationUtils;
import org.apache.commons.io.IOUtils;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;

/**
 * Top component which displays the editor window.
 */
@ConvertAsProperties(
        dtd = "-//latexstudio.editor//Editor//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "EditorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "latexstudio.editor.EditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_EditorAction",
        preferredID = "EditorTopComponent"
)
@Messages({
    "CTL_EditorAction=Editor",
    "CTL_EditorTopComponent=Editor Window",
    "HINT_EditorTopComponent=This is a Editor window"
})
public final class EditorTopComponent extends TopComponent {
    
    private boolean dirty = false;
    private File currentFile; 
    private DbxState dbxState;
    private String latexPath;

    private static final int AUTO_COMPLETE_DELAY = 700;

    public EditorTopComponent() {
        initComponents();
        setName(Bundle.CTL_EditorTopComponent());
        setDisplayName("welcome.tex");
        setToolTipText(Bundle.HINT_EditorTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        
        Preferences pref = NbPreferences.forModule(LaTeXSettingsPanel.class); 
        String path = pref.get("latexPath", ""); 
        pref.addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) { 
                if (evt.getKey().equals("latexPath")) { 
                    latexPath = evt.getNewValue(); 
                } 
            } 
        }); 
        
        latexPath = path;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        rSyntaxTextArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

        rSyntaxTextArea.setColumns(20);
        rSyntaxTextArea.setRows(5);
        rSyntaxTextArea.setSyntaxEditingStyle(org.openide.util.NbBundle.getMessage(EditorTopComponent.class, "EditorTopComponent.rSyntaxTextArea.syntaxEditingStyle")); // NOI18N
        rSyntaxTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rSyntaxTextAreaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                rSyntaxTextAreaKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(rSyntaxTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rSyntaxTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaKeyReleased
        dirty = true;
    }//GEN-LAST:event_rSyntaxTextAreaKeyReleased

    private void rSyntaxTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rSyntaxTextAreaKeyTyped
        if (currentFile == null) return;
        setDisplayName(currentFile.getName() + '*');
    }//GEN-LAST:event_rSyntaxTextAreaKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea rSyntaxTextArea;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void componentOpened() {
        ApplicationUtils.deleteTempFiles();
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setAutoActivationDelay(AUTO_COMPLETE_DELAY);
        ac.setAutoActivationEnabled(true);
        ac.setAutoCompleteEnabled(true);
        ac.install(rSyntaxTextArea);
        
        InputStream is = null;
        try {
            is = getClass().getResource("/latexstudio/editor/resources/welcome.tex").openStream();
            String welcomeMessage = IOUtils.toString(is);
            rSyntaxTextArea.setText(welcomeMessage);
            setDirty(true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void componentClosed() {
    }
    
    public String getEditorContent() {
        return rSyntaxTextArea.getText();
    }
    
    public void setEditorContent(String text) {
        rSyntaxTextArea.setText(text);
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public void undoAction() {
        rSyntaxTextArea.undoLastAction();
    }
    
    public void redoAction() {
        rSyntaxTextArea.redoLastAction();
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        setDisplayName(currentFile.getName());
    }

    public DbxState getDbxState() {
        return dbxState;
    }

    public void setDbxState(DbxState dbxState) {
        this.dbxState = dbxState;
    }
    
    public String getLatexPath() {
        return latexPath;
    }
    
    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.setAutoActivationRules(true, "");

        URL[] urls = new URL[3];
        urls[0] = getClass().getResource("/latexstudio/editor/resources/tex.cwl");
        urls[1] = getClass().getResource("/latexstudio/editor/resources/latex-document.cwl");
        urls[2] = getClass().getResource("/latexstudio/editor/resources/latex-mathsymbols.cwl");

        for (URL url : urls) {
            InputStream is = null;
            try {
                is = url.openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        provider.addCompletion(new BasicCompletion(provider, line.substring(1)));
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        
        return provider;
   }
}
