package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Controller implements Initializable{

    @FXML private Button buttonFolder;
    @FXML private ListView listView;

    @FXML private TextField textFileName;
    @FXML private TextField textTitle;
    @FXML private TextField textAuthor;
    @FXML private TextField textPerformer;
    @FXML private TextField textModerator;
    @FXML private TextField textLink;
    @FXML private TextField textVersion;

    @FXML private ChoiceBox<String> box1;
    @FXML private ChoiceBox<String> box2;
    @FXML private ChoiceBox<String> box3;
    @FXML private ChoiceBox<String> box4;
    @FXML private ChoiceBox<String> box5;
    @FXML private ChoiceBox<String> box6;

    @FXML private CheckBox boxRefren;
    @FXML private TextArea textRefrenSong;
    @FXML private TextArea textRefrenChords;

    @FXML private Button buttonRefren;

    @FXML private TabPane tabPane;

    @FXML private Button buttonCode;

    @FXML private SplitPane splitPanePreview;
    @FXML private TextArea textPreview1;
    @FXML private TextArea textPreview2;

    static String style = "-fx-background-color: #FFB84D";
    static String FILE_NAME_PREFIX = "o!_";

    static String ZWROTKA_TAB_TITLE = "Zwr.";
    static String REFREN_TAB_TITLE = "Ref.";

    private ArrayList<TextArea> refrenPassiveTexts = new ArrayList<>();
    private ArrayList<TextArea> refrenPassiveChords = new ArrayList<>();

    private boolean folderSelected = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> values = FXCollections.observableArrayList(
                "-", "#Angielski", "#Ballada", "#Harcerskie",
                "#Historyczne", "#Kolędy", "#OMiłości", "#Patriotyczne", "#Powstańcze",
                "#PoezjaŚpiewana", "#Popularne", "#Refleksyjne", "#Religijne",
                "#Spokojne", "#Szanty", "#Turystyczne", "#ZBajek", "#Żywe");

        ConnectedComboBox<String> connectedComboBox = new ConnectedComboBox<>(values);
        connectedComboBox.addComboBox(box1);
        connectedComboBox.addComboBox(box2);
        connectedComboBox.addComboBox(box3);
        connectedComboBox.addComboBox(box4);
        connectedComboBox.addComboBox(box5);
        connectedComboBox.addComboBox(box6);

        box1.getSelectionModel().selectFirst();
        box2.getSelectionModel().selectFirst();
        box3.getSelectionModel().selectFirst();
        box4.getSelectionModel().selectFirst();
        box5.getSelectionModel().selectFirst();
        box6.getSelectionModel().selectFirst();

        tabPane.getTabs().get(0).setStyle(style);

        setChordCorrector(textRefrenChords);
        setTextCorrector(textRefrenSong);

        setTextCorrector(textTitle);
        setTextCorrector(textAuthor);
        setTextCorrector(textPerformer);
        setTextCorrector(textModerator);

        textTitle.textProperty().addListener((observable, oldValue, newValue) ->

            textFileName.setText(FILE_NAME_PREFIX + removePolishChars(textTitle.getText()
                    .replace(" ", "_")
                    .replace(",", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "")
                    .replace("?", "")
                    .replace(";", "")
                    .toLowerCase().replaceAll("\\s+","")))
        );

        textRefrenSong.textProperty().addListener((observable, oldValue, newValue) -> {
            for(TextArea textArea : refrenPassiveTexts)
                textArea.setText(textRefrenSong.getText());
        });
        textRefrenChords.textProperty().addListener((observable, oldValue, newValue) -> {
            for(TextArea textArea : refrenPassiveChords)
                textArea.setText(textRefrenChords.getText());
        });

        listView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            String file_name = newValue;

            if(file_name==null)
                return;

            String song_code = readFile(new File(buttonFolder.getText(), file_name).getPath());
            try {
                decode(file_name, song_code);
            }catch (Exception e){
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });
    }

    public void buttonFolderClick(ActionEvent event){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Wybierz folder z piosenkami...");
        chooser.setInitialDirectory(null);
        File dir = chooser.showDialog(Main.getPrimaryStage());

        if(dir!=null) {
            buttonFolder.setText(dir.getPath());

            fillListView();
            folderSelected = true;
        }
    }

    private void fillListView(){
        listView.getItems().clear();
        File dir = new File(buttonFolder.getText());
        File files[] = dir.listFiles();
        String[] file_names = new String[files.length];
        for (int i=0; i<files.length; i++)
            file_names[i] = files[i].getName();

        Arrays.sort(file_names);
        listView.getItems().addAll(file_names);
    }

    static String readFile(String path)
    {
        String result = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            result = new String(encoded, "utf8");
        }catch (Exception e){}
        return result;
    }

    public void boxRefrenCheck(ActionEvent e){
        if(boxRefren.isSelected()) {
            textRefrenSong.setDisable(false);
            textRefrenChords.setDisable(false);
            buttonRefren.setDisable(false);
        }else{
            textRefrenSong.setDisable(true);
            textRefrenChords.setDisable(true);
            buttonRefren.setDisable(true);
        }
    }

    public void buttonNewClick(ActionEvent e){
        textFileName.clear();
        textTitle.clear();
        textAuthor.clear();
        textPerformer.clear();
        //textModerator.clear();
        textLink.clear();
        textVersion.setText("1");

        textRefrenSong.clear();
        textRefrenChords.clear();

        box1.getSelectionModel().selectFirst();
        box2.getSelectionModel().selectFirst();
        box3.getSelectionModel().selectFirst();
        box4.getSelectionModel().selectFirst();
        box5.getSelectionModel().selectFirst();
        box6.getSelectionModel().selectFirst();

        tabPane.getTabs().remove(1, tabPane.getTabs().size()-1);
        boxRefren.setSelected(true);
        buttonRefren.setDisable(false);
        textRefrenSong.setDisable(false);
        textRefrenChords.setDisable(false);

    }

    public void buttonZwrotkaClick(ActionEvent e){
        addZwrotka("", "");
    }
    public void buttonRefrenClick(ActionEvent e){
        addRefren();
    }

    private void setCopyChordsButtonClickability(Tab tab){
        int tab_zwrotka_count = 0;
        for (int i = 1; i < tabPane.getTabs().size() - 1; i++) {
            if (tabPane.getTabs().get(i).getText().contains(ZWROTKA_TAB_TITLE))
                tab_zwrotka_count++;

            if(tabPane.getTabs().get(i) == tab)
                break;
        }
        if(tab_zwrotka_count==1)
            getButtonCopyChords(tab).setDisable(true);
        else {
            getButtonCopyChords(tab).setDisable(false);
        }
    }

    private void addZwrotka(String song, String chords){
        Tab tab = addTab(song, chords, ZWROTKA_TAB_TITLE);
        tab.setStyle(style);

        setCopyChordsButtonClickability(tab);
        setCopyChords(getButtonCopyChords(tab), getTabTextChords(tab));

        setChordCorrector(getTabTextChords(tab));
        setTextCorrector(getTabTextSong(tab));

        buttonPreviewClick(null);
    }
    private void addRefren(){

        Tab tab;
        tab = addTab(textRefrenSong.getText(), textRefrenChords.getText(), REFREN_TAB_TITLE);

        getButtonCopyChords(tab).setVisible(false);
        getTabTextSong(tab).setDisable(true);
        getTabTextChords(tab).setDisable(true);

        getTabTextSong(tab).setText(textRefrenSong.getText());
        getTabTextChords(tab).setText(textRefrenChords.getText());

        refrenPassiveTexts.add(getTabTextSong(tab));
        refrenPassiveChords.add(getTabTextChords(tab));

        buttonPreviewClick(null);
    }

    private Tab addTab(String song, String chords, String title){
        final Tab tab;

        try { tab = FXMLLoader.load(this.getClass().getResource("tab.fxml"));

            if(tab!=null) {
                tab.setText(title);
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
                tabPane.getSelectionModel().select(tab);

                getTabTextSong(tab).setText(song);
                getTabTextChords(tab).setText(chords);

                tab.setOnClosed(o -> {
                    for (Tab _tab : tabPane.getTabs())
                        if (_tab.getText().contains(ZWROTKA_TAB_TITLE))
                            setCopyChordsButtonClickability(_tab);

                    buttonPreviewClick(null);
                });

                tab.selectedProperty().addListener((observable, oldValue, newValue) -> {

                    if(!newValue) return;

                    KeyCombination cntrlLeft = new KeyCodeCombination(KeyCode.LEFT, KeyCodeCombination.CONTROL_DOWN);
                    KeyCombination cntrlRight = new KeyCodeCombination(KeyCode.RIGHT, KeyCodeCombination.CONTROL_DOWN);

                    tabPane.setOnKeyPressed(event -> {
                        if(cntrlLeft.match(event)){
                            int index = tabPane.getTabs().indexOf(tab);
                            if(index <= 1)
                                return;

                            tabPane.getTabs().remove(tab);
                            tabPane.getTabs().add(index-1, tab);
                            tabPane.getSelectionModel().select(index-1);
                        }
                        else if(cntrlRight.match(event)){
                            int index = tabPane.getTabs().indexOf(tab);
                            if(index >= tabPane.getTabs().size()-2)
                                return;

                            tabPane.getTabs().remove(tab);
                            tabPane.getTabs().add(index+1, tab);
                            tabPane.getSelectionModel().select(index+1);
                        }

                        for (Tab _tab : tabPane.getTabs())
                            if (_tab.getText().contains(ZWROTKA_TAB_TITLE))
                                setCopyChordsButtonClickability(_tab);

                        buttonPreviewClick(null);
                    });

                });

            }
            buttonPreviewClick(null);

            return tab;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private void decode(String file_name, String song) throws Exception{

        buttonNewClick(null);

        song = song.replace("\r", "");
        song = song.replace("\n<", "<");

        String[] parts = song.split("<", -1);
        if(parts[0].split("\n", -1).length<6)
            throw new Exception("Błąd kodowania piosenki.");

        String[] lines = parts[0].split("\n", -1);

        textTitle.setText(lines[0]);
        textFileName.setText(file_name); //has to be second (textTitleChangedListener)

        textVersion.setText(lines[1]);
        if (!lines[2].equals("0"))
            textAuthor.setText(lines[2]);
        if (!lines[3].equals("0"))
            textPerformer.setText(lines[3]);

        textModerator.setText(lines[4]);

        if (lines.length>5 && !lines[5].equals("0"))
            textLink.setText(lines[5]);

        if (lines.length>6 && lines[6].length()!=0) {
            String[] tags = lines[6].split(";");
            if (tags.length > 0)
                box1.setValue(tags[0]);
            if (tags.length > 1)
                box2.setValue(tags[1]);
            if (tags.length > 2)
                box3.setValue(tags[2]);
            if (tags.length > 3)
                box4.setValue(tags[3]);
            if (tags.length > 4)
                box5.setValue(tags[4]);
            if (tags.length > 5)
                box6.setValue(tags[5]);
        }

        boolean has_refern;
        if(parts[1].length()!=0) {
            has_refern = true;
            String[] refren_elements = parts[1].split(">");
            textRefrenChords.setText(decodeChords(refren_elements[0]));
            textRefrenSong.setText(refren_elements[1]);
        }else
            has_refern = false;

        boxRefren.setSelected(has_refern);
        boxRefrenCheck(null);

        String first_zwrotka_chords = null;
        for(int i=2; i<parts.length; i++)
        {
            if (parts[i].length() == 0) {
                addRefren();
                continue;
            }

            String zwrotka_elements[] = parts[i].split(">", -1);
            if(first_zwrotka_chords==null)
                first_zwrotka_chords = zwrotka_elements[0];

            if (zwrotka_elements[0].equals("1"))
                zwrotka_elements[0] = first_zwrotka_chords;

            addZwrotka(zwrotka_elements[1], decodeChords(zwrotka_elements[0]));
        }
        tabPane.getSelectionModel().select(1);
        buttonPreviewClick(null);
    }

    public void buttonCodeClick(ActionEvent e){
        textPreview1.setText(convertToCode());
        splitPanePreview.setDividerPositions(1);
    }

    public void buttonPreviewClick(ActionEvent e){

        String song = "";
        String chords = "";

        for(int i=1; i<tabPane.getTabs().size()-1; i++){
            if(tabPane.getTabs().get(i).getText().contains(ZWROTKA_TAB_TITLE)) {
                song += getTabTextSong(i).getText() + "\n\n";
                chords += getTabTextChords(i).getText() + "\n\n";

                int text_lines = getTabTextSong(i).getText().split("\n").length;
                int chods_lines = getTabTextChords(i).getText().split("\n").length;

                for(int j=0; j<chods_lines-text_lines; j++)
                    song += "\n";
                for(int j=0; j<text_lines-chods_lines; j++)
                    chords += "\n";

            }else if(boxRefren.isSelected()){
                song += "\t" + textRefrenSong.getText().replace("\n", "\n\t") + "\n\n";
                chords += textRefrenChords.getText() + "\n\n";

                int text_lines = textRefrenSong.getText().split("\n").length;
                int chods_lines = textRefrenChords.getText().split("\n").length;

                for(int j=0; j<chods_lines-text_lines; j++)
                    song += "\n";
                for(int j=0; j<text_lines-chods_lines; j++)
                    chords += "\n";
            }
            //int diff = song.split("\n").length - chords.split("\n").length;

            //if(diff>0)
            //    for(int j = 0; j<diff; j++)
            //        chords += "\n";
            //else
            //    for(int j = 0; j<diff; j++)
            //        song += "\n";
        }

        if(song.length()>2)
            song = song.substring(0, song.length()-2);

        if(chords.length()>2)
            chords = chords.substring(0, chords.length()-2);

        splitPanePreview.setDividerPositions(0.75f);

        textPreview1.setText(song);
        textPreview2.setText(chords);

    }

    private String encodeChords(String chords){
        String output = "";
        String chordlines[] = chords.split("\n", -1);

        for(int i=0; i<chordlines.length; i++){
            boolean foundRepetiton = false;
            for(int j=0; j<i; j++)
                if(chordlines[i].equals(chordlines[j])) {
                    output += j + "\n";
                    foundRepetiton = true;
                    break;
                }

                if(!foundRepetiton)
                    output += chordlines[i] + "\n";
        }

        if(output.length()>0)
            output = output.substring(0, output.length()-1);

        return output;
    }

    private String decodeChords(String code){
        String output = "";
        String codeLines[] = code.split("\n");

        for(int i=0; i<codeLines.length; i++) {

            Character firstChar;
            if(codeLines[i].length()>0)
                firstChar = codeLines[i].charAt(0);
            else
                firstChar = null;

            if (codeLines[i].length() > 0 && number.contains(firstChar)) {
                int index = Integer.parseInt(codeLines[i]);
                output += codeLines[index] + "\n";
            }else
                output += codeLines[i] + "\n";
        }


        if(output.length()>0)
            output = output.substring(0, output.length()-1);

        return output;
    }

    private String convertToCode() {
        String first_zwrotka_chords = null;

        String text = textTitle.getText() + '\n';
        text += (textVersion.getText().length() == 0 ? "1" : textVersion.getText()) + "\n";
        text += (textAuthor.getText().length() == 0 ? "" : textAuthor.getText()) + "\n";
        text += (textPerformer.getText().length() == 0 ? "" : textPerformer.getText()) + "\n";
        text += textModerator.getText() + "\n"; //moderator
        text += textLink.getText()==null?"\n":(textLink.getText() + "\n"); //youtube link

        ArrayList<String> tags = new ArrayList<>();

        if(box1.getSelectionModel().getSelectedIndex()!=0)
            tags.add(box1.getValue());
        if(box2.getSelectionModel().getSelectedIndex()!=0)
            tags.add(box2.getValue());
        if(box3.getSelectionModel().getSelectedIndex()!=0)
            tags.add(box3.getValue());
        if(box4.getSelectionModel().getSelectedIndex()!=0)
            tags.add(box4.getValue());
        if(box5.getSelectionModel().getSelectedIndex()!=0)
            tags.add(box5.getValue());
        if(box6.getSelectionModel().getSelectedIndex()!=0)
            tags.add(box6.getValue());

        Collections.sort(tags);

        for(String tag : tags)
            text+=tag + ";";

        if(text.charAt(text.length()-1)==';')
            text = text.substring(0, text.length()-1);

        text += "<";
        if (boxRefren.isSelected() && (textRefrenChords.getText().length()!=0 || textRefrenSong.getText().length()!=0)) {
            text += encodeChords(textRefrenChords.getText());
            text += ">" + textRefrenSong.getText();
        }

        int items = tabPane.getTabs().size();
        for (int i = 1; i < items-1; i++) {

            if (tabPane.getTabs().get(i).getText().contains(ZWROTKA_TAB_TITLE)) {
                if(first_zwrotka_chords!=null){
                    if(first_zwrotka_chords.equals(getTabTextChords(i).getText()))
                        text+="<1";
                    else
                        text += "<" + encodeChords(getTabTextChords(i).getText());
                }else {
                    first_zwrotka_chords = getTabTextChords(i).getText();
                    text += "<" + encodeChords(getTabTextChords(i).getText());
                }

                text +=  ">" + getTabTextSong(i).getText();

            } else if(boxRefren.isSelected())
                text += "<";
        }

        return text;
    }

    private TextArea getTabTextSong(int position){
        Tab tab = tabPane.getTabs().get(position);
        return getTabTextSong(tab);
    }

    private TextArea getTabTextSong(Tab tab){
        SplitPane splitPane = (SplitPane) tab.getContent();
        AnchorPane anchorPane1 = (AnchorPane) splitPane.getItems().get(0);
        return (TextArea) anchorPane1.getChildren().get(0);
    }

    private TextArea getTabTextChords(int position){
        Tab tab = tabPane.getTabs().get(position);
        return getTabTextChords(tab);
    }

    private TextArea getTabTextChords(Tab tab){
        SplitPane splitPane = (SplitPane) tab.getContent();
        AnchorPane anchorPane2 = (AnchorPane) splitPane.getItems().get(1);
        BorderPane borderPane = (BorderPane) anchorPane2.getChildren().get(0);
        return (TextArea) borderPane.getCenter();
    }

    private Button getButtonCopyChords(Tab tab){
        for(Tab _tab : tabPane.getTabs()) {
            if(_tab!=tab)
                continue;
            SplitPane splitPane = (SplitPane) _tab.getContent();
            AnchorPane anchorPane2 = (AnchorPane) splitPane.getItems().get(1);
            BorderPane borderPane = (BorderPane) anchorPane2.getChildren().get(0);
            return (Button) borderPane.getTop();
        }
        buttonPreviewClick(null);
        return null;
    }

    final List<String> chords = Arrays.asList("C", "c", "Cis", "cis", "D", "d", "Dis", "dis", "E", "e", "F", "f", "Fis", "fis", "G", "g", "Gis", "gis", "A", "a", "B", "b", "H", "h");
    final List<Character> number = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private void setChordCorrector(TextArea textArea){

        textArea.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue){

                if(textArea.getText()==null || textArea.getText().length()==0)
                    return;

                String string = "";

                String text = textArea.getText();
                String[] lines = text.split("\n", -1);

                for(String line : lines){
                    String[] elements = line.split(" ");

                    if(line.length()>0) {
                        for (String e : elements)
                            if (chords.contains(e) || (chords.contains(e.substring(0, e.length() - 1)) && number.contains(e.charAt(e.length() - 1))))
                                string += e + " ";

                        if(string.length()>0)
                            string = string.substring(0, string.length() - 1);
                    }
                    string += "\n";
                }

                while(string.length()>0 && string.charAt(string.length()-1)=='\n')
                    string = string.substring(0, string.length()-1);

                textArea.setText(string);
            }
            buttonPreviewClick(null);

        });

        textArea.textProperty().addListener((observable, oldValue, newValue) ->
                buttonPreviewClick(null)
        );
    }

    final List<Character> non_endline_chars = Arrays.asList('.', ',', '!', ':', ';', ' ', '\t');
    private void setTextCorrector(TextArea textArea){

        textArea.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                if(textArea.getText()==null || textArea.getText().length()==0)
                    return;

                String string = "";

                String text = textArea.getText();
                String[] lines = text.split("\n", -1);


                for(String line : lines) {
                    while (line.length() > 0 && non_endline_chars.contains(line.charAt(line.length() - 1)))
                        line = line.substring(0, line.length() - 1);

                    while (line.length() > 0 && line.charAt(0)==' ')
                        line = line.substring(1);

                    if (line.length() > 0)
                        line = line.substring(0, 1).toUpperCase() + line.substring(1);

                    string += line + "\n";
                }
                while(string.length()>0 && string.charAt(string.length()-1)=='\n')
                    string = string.substring(0, string.length()-1);

                textArea.setText(string);
            }

            buttonPreviewClick(null);
        });

        textArea.textProperty().addListener((observable, oldValue, newValue) ->
                buttonPreviewClick(null)
        );
    }

    private void setTextCorrector(TextField textField){
        textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                String text = textField.getText();
                while (text.length() > 0 && non_endline_chars.contains(text.charAt(text.length() - 1)))
                    text = text.substring(0, text.length() - 1);

                while (text.length() > 0 && text.charAt(0) == ' ')
                    text = text.substring(1);
                textField.setText(text);
            }
        });
    }

    private void setCopyChords(Button button, TextArea textArea){
        button.setOnAction(event -> {

            for (int i = 1; i < tabPane.getTabs().size() - 1; i++) {
                if (tabPane.getTabs().get(i).getText().contains(ZWROTKA_TAB_TITLE)) {
                    textArea.setText(getTabTextChords(i).getText());
                    return;
                }
            }
        });
    }

    public void resaveAll(){
        for(int i=0; i< listView.getItems().size(); i++){

            String file_name = (String) listView.getItems().get(i);
            String code = readFile(new File(buttonFolder.getText(), file_name).getPath());
            //code = code.replace("\n<", "<");
            try {
                decode(file_name, code);
            }catch (Exception e){}

            String save_path = buttonFolder.getText() + "2/" + FILE_NAME_PREFIX + file_name.substring(2);

            String new_code = convertToCode();
            try (PrintWriter out = new PrintWriter(save_path)) {
                out.print(new_code);
            } catch (Exception ex) {}
        }
    }

    public void buttonCreateDataFileClick(ActionEvent e){
        //resaveAll();
        //return;

        String _data = "";

        List<String> codes = new ArrayList<>();
        HashMap<String, String> codeMap = new HashMap<>();

        for(int i=0; i< listView.getItems().size(); i++){
            String file_name = (String) listView.getItems().get(i);
            if(file_name.equals("_data")) continue;
            try{
                String code = readFile(new File(buttonFolder.getText(), file_name).getPath()).split("<")[0];
                String title = code.split("\n", -1)[0];
                String version = code.split("\n", -1)[1];
                String author = code.split("\n", -1)[2];
                String performer = code.split("\n", -1)[3];
                String moderator = code.split("\n", -1)[4];
                String tags = code.split("\n", -1)[6].replace(";", " ");

                codes.add(code);
                codeMap.put(code, file_name);
            }catch (Exception exception){}
        }

        Collections.sort(codes, (s1, s2) -> Comparator.compare(s1, s2));

        for(int i=0; i< codes.size(); i++){

            String code = codes.get(i);
            String file_name = codeMap.get(code);
            String title = code.split("\n", -1)[0];
            String version = code.split("\n", -1)[1];
            String author = code.split("\n", -1)[2];
            String performer = code.split("\n", -1)[3];
            String moderator = code.split("\n", -1)[4];
            String tags = code.split("\n", -1)[6].replace(";", " ");

            _data += "\n" + file_name + ";" + title + ";" + version + ";" + author  + ";" + performer + ";" + moderator + ";" + tags;

        }

        String save_path = buttonFolder.getText() + "/" + "_data";

        try (PrintWriter out = new PrintWriter(save_path, StandardCharsets.UTF_8)) {
            out.print(_data);
            new Alert(Alert.AlertType.CONFIRMATION, "Stworzono plik \"_data\".").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }

    }

    public void buttonSaveClick(ActionEvent e){

//        buttonLoadClick();

//        resaveAll();

        if(!folderSelected)
            buttonFolderClick(null);

        if(folderSelected) {

            String file_name = textFileName.getText();
            if(file_name.length()<2 || !file_name.substring(0, 3).equals(FILE_NAME_PREFIX))
                file_name = FILE_NAME_PREFIX + file_name;

            File dir = new File(buttonFolder.getText());
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file_name.equals(file.getName())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Czy chcesz nadpisać istniejący plik?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) break;
                    else return;

                }
            }

            try (PrintWriter out = new PrintWriter(buttonFolder.getText() + "/" + file_name, StandardCharsets.UTF_8)) {
                out.print(convertToCode());
                new Alert(Alert.AlertType.CONFIRMATION, "Zapisano piosenkę.").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
            fillListView();
            listView.getSelectionModel().select(textFileName.getText());


        }

    }

    private String removePolishChars(String string){
        return string.toLowerCase()
                .replace('ą', 'a')
                .replace('á', 'a')
                .replace('ć', 'c')
                .replace('ę', 'e')
                .replace('é', 'e')
                .replace('í', 'i')
                .replace('ł', 'l')
                .replace('ń', 'n')
                .replace('ó', 'o')
                .replace('ö', 'o')
                .replace('ő', 'o')
                .replace('ś', 's')
                .replace('ú', 'u')
                .replace('ü', 'u')
                .replace('ű', 'u')
                .replace('ź', 'z')
                .replace('ż', 'z');
    }
}