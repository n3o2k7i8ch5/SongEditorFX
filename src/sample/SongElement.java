package sample;

/**
 * Created by Kisiel on 08.11.2016.
 */

public class SongElement {

    private String[] text_lines;
    private String[] chord_lines;

    String code;
    private boolean has_chords = false;

    SongElement(String code) throws Exception{
        this.code = code;
        String[] lines = code.split("\n", -1);

        if (lines.length % 2 == 1)
            throw new Exception("Błąd parzystości linijek.");

        String[] chord_lines = new String[lines.length/2];
        String[] text_lines = new String[lines.length/2];

        for (int j = 0; j < lines.length/2; j++) {
            chord_lines[j] = lines[2*j];
            text_lines[j] = lines[2*j + 1];
        }

        this.text_lines = text_lines;
        this.chord_lines = chord_lines;

        if(getChordsAsString().replace("\n", "").length()==0)
            has_chords = false;
        else
            has_chords = true;

    }

    SongElement(String[] text_lines, String[] chord_lines){
        this.text_lines = text_lines;
        this.chord_lines = chord_lines;

        if(getChordsAsString().replace("\n", "").length()==0)
            has_chords = false;
        else
            has_chords = true;
    }

    public boolean hasChords(){return has_chords;}

    public String getText(boolean with_tab){
        return getString(text_lines, with_tab);
    }

    public String getChordsAsString(){
        return getString(chord_lines, false);
    }

    private String getString(String[] lines, boolean with_tab){
        String string = "";

        if(lines==null)
            return string;

        for(int i=0; i<lines.length; i++) {

            if(lines[i] == null)
                lines[i] = "";

            if (with_tab)
                string += '\t' + lines[i] + '\n';
            else
                string += lines[i] + '\n';
        }
        if(string.length()>0)
            string = string.substring(0, string.length()-1);

        return string;
    }

    public String[] getChords(){
        return chord_lines;
    }

    public String[] getText(){
        return text_lines;
    }
}
