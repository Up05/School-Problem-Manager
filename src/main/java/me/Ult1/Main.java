package me.Ult1;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import static com.diogonunes.jcolor.Attribute.*;
import com.opencsv.*;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


class Headers {
    public static final int
        NAME      = 0,
        DATE      = 1,
        PATH      = 2,
        FULL_NAME = 3,
        STAGE     = 4;
}

public class Main {

    static List<String[]> rows;
    static CSVWriter writer;
    static Path DATA_PATH;
//        = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile().substring(1)).getParent();
    static final Path problem_file_path = DATA_PATH.resolve("problems.csv");

    public static void main(String[] args) throws Exception {

        AppDirs dirs = AppDirsFactory.getInstance();
        DATA_PATH = Paths.get(dirs.getUserDataDir("SchoolProblemManager", null, "Ult1"));

        if(args.length < 5){
            String[] temp = new String[]{"", "", "", "", ""};
            for(int i = 0; i < temp.length; i ++) {
                if(args.length > i && !args[i].isEmpty()) temp[i] = args[i];
            }
            args = temp;
        }

        Main main = new Main();

        if(!problem_file_path.toFile().exists()) Files.createFile(problem_file_path);

        CSVReader reader = new CSVReaderBuilder(new FileReader(problem_file_path.toFile())).withSkipLines(1).build();
        rows = reader.readAll();
        reader.close();

        writer = new CSVWriter(new FileWriter(problem_file_path.toFile(), true));

        if(!main.handleArgs(args)) System.out.println("Please use: " + Ansi.colorize("spm help", GREEN_TEXT(), BOLD()) + " for help.");

//        main.send();
//        main.mark("something4");
//        main.list();
//        main.create("amazing_project", "10-40", "Totally Amazing Project of Amazingness");
//        main.open("amazing_project");
//        main.compile("amazing_project");
//        main.backup("load", "problems 2022-10-31.csv");
    }

    public boolean handleArgs(String[] args) throws Exception{
        if(!args[1].isEmpty()){
            switch (args[0]){
                case "create":
                case "c":
                    return (!args[3].isEmpty()) ? create(args[1], args[2], args[3]) : create(args[1], args[2], "none");
                case "compile":
                case "cp":
                    return compile(args[1]);
                case "open":
                case "o":
                    return open(args[1]);
                case "mark":
                case "m":
                    return mark(args[1], 1);
                case "send":
                    return send(args[1]);
                case "remove":
                case "r":
                    return remove(args[1]);
                case "backup":
                case "b":
                    return backup(args[1], args[2]);
            }
        } else {
            String nameOfNewest = null;
            if(rows.size() > 0)
                nameOfNewest = rows.get(rows.size() - 1)[Headers.NAME];
            switch (args[0]){
                case "compile":
                case "cp":
                    System.out.println("Selected: " + nameOfNewest);
                    return compile(nameOfNewest);
                case "open":
                case "o":
                    System.out.println("Selected: " + nameOfNewest);
                    return open(nameOfNewest);
                case "mark":
                case "m":
                    System.out.println("Selected: " + nameOfNewest);
                    return mark(nameOfNewest, 1);
                case "send":
                    System.out.println("Selected: " + nameOfNewest);
                    return send(nameOfNewest);
                case "list":
                case "l":
                    return list();
                case "remove":
                case "r":
                    System.out.println(Ansi.colorize("You cannot remove the latest project! Please type the name of the project instead!", RED_TEXT()));
                    return true;
                case "help":
                case "h":
                    return help();
            }



        }
        return false;
    }

    public boolean create(String name, String date, String fullName) throws IOException {
        Path dir = DATA_PATH.resolve("projects").resolve(name);
        if(dir.toFile().exists()) {
            System.out.println("A directory with this name already exists!");
            return false;
        }
        dir.toFile().mkdirs();

        {
            String main_cpp = (
                    "#include <iostream>\n" +
                    "#include <fstream>\n" +
                    "#include <iomanip>\n\n" +
                    "using namespace std;\n\n" +
                    "int main(){\n" +
                    "    ifstream fd(\"$INFILE\");\n" +
                    "    ofstream fr(\"$OUTFILE\");\n\n" +
                    "    return 0;\n" +
                    "}"
            ).replace("$INFILE", name + ".txt").replace("$OUTFILE", name + "Rez.txt");

            Files.createFile(dir.resolve("main.cpp"));
            Files.write(dir.resolve("main.cpp"), main_cpp.getBytes(StandardCharsets.UTF_8));

            Files.copy (
//                Objects.requireNonNull(Main.class.getResourceAsStream("/template/compile.bat")),
                DATA_PATH.resolve("template/compile.bat"), // TODO
                Paths.get(dir + "/compile.bat")
            );

            Files.createFile(dir.resolve(name + ".txt"));
        }
        writer.writeNext(new String[]{name, date, dir.toString(), fullName, "0"}, false);
        writer.close();

        return true;
    }

    public boolean compile(String name) throws IOException, InterruptedException {
//        String a = DATA_PATH + "/projects/" + name + "/";
//        String b = DATA_PATH + "/compiled/" + name + "/";
        Path a = DATA_PATH.resolve("projects").resolve(name);
        Path b = DATA_PATH.resolve("compiled").resolve(name);

        if(!a.resolve(name).resolve("Rez.txt").toFile().exists()) {
//        if(!new File(a + name + "Rez.txt").exists()) {
            System.out.println(Ansi.colorize("The result file hasn't been created yet! The project shouldn't be compiled before having been finished or at least run!", RED_TEXT()));
            System.out.println(Ansi.colorize(a + name + "Rez.txt", BLUE_TEXT(), UNDERLINE()) + " Doesn't exist!");
            return false;
        }

        b.toFile().mkdirs();

        Files.copy(a.resolve(name + ".txt"),    b.resolve(name + ".txt"),    StandardCopyOption.REPLACE_EXISTING);
        Files.copy(a.resolve(name + "Rez.txt"), b.resolve(name + "Rez.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(a.resolve("main.cpp"),       b.resolve("main.cpp"),       StandardCopyOption.REPLACE_EXISTING);

        {
            File[] pdfFiles = a.toFile().listFiles((dir, name1) -> name1.contains("pdf"));
            if (pdfFiles.length != 0)
                Files.copy(Paths.get(pdfFiles[0].getAbsolutePath()), b.resolve("main.cpp"), StandardCopyOption.REPLACE_EXISTING);
        }

        String date = "";
        for(String[] row : rows)
            if(row[Headers.NAME].equals(name)) date = row[Headers.DATE];

        StringBuilder otherProblems = new StringBuilder(), folderNames = new StringBuilder();
        for(String[] row : rows)
            if(row[Headers.DATE].equals(date)){
                otherProblems.append(DATA_PATH.resolve(DATA_PATH).resolve("compiled").resolve(row[Headers.NAME]));
                folderNames.append(row[Headers.NAME]).append(' ');
            }

        Process compression = Runtime.getRuntime().exec(
        "\"C:\\Program Files\\7-Zip\\7z.exe\" " +
            "a \"" + DATA_PATH.resolve("compiled").resolve("Augustinas - " + date + " sprendimai.zip") + "\" " +
            otherProblems
        );
        compression.waitFor();
        if(compression.exitValue() != -1) {
            System.out.println(
                "Problems: " + Ansi.colorize(folderNames.toString(), BLUE_TEXT()) + "compiled successfully into: \n" +
                Ansi.colorize(DATA_PATH.resolve("compiled").resolve("Augustinas - " + date + " sprendimai.zip").toString(),
                BLUE_TEXT(), UNDERLINE())
            );

            Desktop.getDesktop().open(DATA_PATH.resolve("compiled").toFile());
        }

        // "C:\Program Files\7-Zip\7z.exe"
        // 7z add archv_name ...files_to_add
        // 7z a my_archive.zip *.txt *.cpp
        return true;
    }

    public boolean open(String name) throws IOException {
//        System.out.println("code " + (jar_dir + "projects/" + name + "/").replaceAll("/", "\\\\").substring(1));
        if(!DATA_PATH.resolve("projects").resolve(name).toFile().exists()) {
            System.out.println("Project by the name: \"" + name + "\" doesn't exist in \"" + DATA_PATH.resolve("projects") + '!');
            return false;
        }
        new ProcessBuilder(
                "C:\\Users\\Augustas\\AppData\\Local\\Programs\\Microsoft VS Code\\bin\\code.cmd",
                DATA_PATH.resolve("projects").resolve(name).toString().replaceAll("/", "\\\\")
        ).start();

        System.out.println("Project at the path: \"" + Ansi.colorize((DATA_PATH + "/projects/" + name).replaceAll("\\\\", "/"), BLUE_TEXT(), UNDERLINE()) + "\" has been opened!");

        return true;
    }

    public boolean mark(String name, int level) throws IOException {
        CSVWriter rewriter = new CSVWriter(
                new FileWriter(problem_file_path),
                CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END
        );
        rewriter.writeNext(new String[]{"name", "date", "path", "full_name", "stage"});
        for(String[] row : rows)
            if(row[Headers.NAME].equalsIgnoreCase(name)) row[Headers.STAGE] = "" + level;
        rewriter.writeAll(rows);
        rewriter.close();
        return true;
    }

    public boolean send(String name) throws IOException {
//        new Mail().setHeader("TEST header").addBody("TEST body").send();
        String date = "";
        for(String[] row : rows)
            if(row[Headers.NAME].equals(name)) date = row[Headers.DATE];

        for(String[] row : rows)
            if(row[Headers.DATE].equals(date) && row[Headers.STAGE].equals("1"))
                mark(name, 2);

        System.out.println(Ansi.colorize("Successfully marked problems with the same date as the given problem as sent.", BRIGHT_GREEN_TEXT(), BOLD()));

        System.err.println("Exception in com.Google.Security.F_ckYouException:");
        System.err.println("\"There's an option, that makes an account less secure!? KILL IT! They'll hurt themselves with it!\" -Google");

        Runtime.getRuntime().exec("explorer.exe /select,\"" + DATA_PATH.replaceAll("/", "\\") + "\\compiled\\Augustinas - " + date + " sprendimai.zip\"");
        return true;
    }

    public boolean list(){
        System.out.println(Ansi.colorize(DATA_PATH, BLUE_TEXT(), ITALIC(), UNDERLINE()));
        if(rows.size() < 1) System.out.println(Ansi.colorize("There are no projects listed in the problems.csv!", RED_TEXT(), BOLD(), ITALIC()));

        AnsiFormat stageFormat = new AnsiFormat(GREEN_TEXT(), ITALIC());
        int[] max = new int[5];

        for(String[] row : rows)
            for(int i = 0; i < max.length; i ++) {
                if(i == Headers.PATH) {
                    String a = row[i]; int index = a.indexOf("projects");
                    if(index != -1) {
                        a = "~/" + a.substring(index);
                        if(a.length() > max[i])
                            max[i] = a.length();
                        continue;
                    }
                }
                if (row[i].length() > max[i])
                    max[i] = row[i].length();
            }

        for(int i = 0; i < max.length; i ++) max[i] ++; // +1 space char

        for(String[] row : rows){

            for(int i = 0; i < row.length - 1; i ++){
                AnsiFormat format = new AnsiFormat(RED_TEXT());
                     if(i == Headers.PATH)      format = new AnsiFormat(BLUE_TEXT());
                else if(i == Headers.FULL_NAME) format = new AnsiFormat(ITALIC(), CYAN_TEXT());

                else if(row[Headers.STAGE].equals("1")) format = new AnsiFormat(BOLD(), YELLOW_TEXT());
                else if(row[Headers.STAGE].equals("2")) format = new AnsiFormat(ITALIC(), BRIGHT_BLACK_TEXT());

                if(i == Headers.PATH) {
                    String a = row[i];
                    int index = a.indexOf("projects");
                    if(index != -1)
                        a = "~/" + a.substring(index);
                    System.out.print(format.format(Utils.limit(a, max[i])));
                } else
                    System.out.print(format.format(Utils.limit(row[i], max[i])));
            }

            String stage = "unknown!";
                 if(row[4].equals("0")) stage = "created";
            else if(row[4].equals("1")) stage = "marked as done";
            else if(row[4].equals("2")) stage = "sent";
            System.out.println(stageFormat.format(stage));
        }
        return true;
    }

    public boolean remove(String name) throws IOException {
        CSVWriter rewriter = new CSVWriter(
                new FileWriter(problem_file_path),
                CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END
        );
        rewriter.writeNext(new String[]{"name", "date", "path", "full_name", "stage"});
        for(String[] row : rows) {
            row[Headers.PATH] = row[Headers.PATH].replaceAll("\\\\", "/");
            if (!row[Headers.NAME].equalsIgnoreCase(name)) rewriter.writeNext(row);
            else {
                System.out.println("delete path: " + row[Headers.PATH]);
                if (
                        Files.walk(Paths.get(row[Headers.PATH]))
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .allMatch(File::delete)
                )
                    System.out.println(Ansi.colorize("Project has been unlisted & deleted", GREEN_TEXT()));
                else
                    System.out.println(Ansi.colorize("Project has been unlisted, but, at least, one file failed to be deleted!", RED_TEXT()));
            }
        }
        rewriter.close();
        return true;
    }

    public boolean backup(String command, String filename) throws IOException {
        if(command.equalsIgnoreCase("make")){
            File backupsDir = new File(DATA_PATH + "/backups");
            backupsDir.mkdirs();
            Path backupPath;
            if(filename.isEmpty()) {
                backupPath = Paths.get(backupsDir + "/problems " + (new SimpleDateFormat("yyyy-MM-dd").format(new Date())) + ".csv");
                int index = 2;
                while (Files.exists(backupPath) || index > 50) {
                    backupPath = Paths.get(backupsDir + "/problems " + (new SimpleDateFormat("yyyy-MM-dd").format(new Date())) + " " + index + ".csv");
                    index ++;
                }
            } else {
                backupPath = Paths.get(backupsDir + "/" + filename);
                if(Files.exists(backupPath)) {
                    System.out.println("A backup file with that name (\"" + filename + "\") already exists!");
                    return false;
                }
            }

            Files.copy(Paths.get(problem_file_path), backupPath);
            System.out.println(Ansi.colorize("Created new backup at: \"" + backupPath + '"', GREEN_TEXT()));

        } else if(command.equalsIgnoreCase("load")){
            File backupsDir = new File(DATA_PATH + "/backups");
            boolean exists = false;
            for(String _filename : Objects.requireNonNull(backupsDir.list())){
                if (_filename.equals(filename)) {
                    exists = true;
                    break;
                }
            }
            if(!exists) {
                System.out.println("The file (\"" + filename + "\") doesn't exist! Example of a correct filename would be: \"problems 2022-10-31.csv\"");
                return false;
            }

            Files.copy(Paths.get(backupsDir + "/" + filename), Paths.get(problem_file_path), StandardCopyOption.REPLACE_EXISTING);

        } else if(command.equalsIgnoreCase("open")){
            if(new File(DATA_PATH + "/backups").exists())
                Desktop.getDesktop().open(new File(DATA_PATH + "/backups"));
            else {
                System.out.println("The backups folder doesn't exist! It (probably) hasn't been created yet.");
                return false;
            }
        } else {
            System.out.println(Ansi.colorize("The command was not recognized! Please use one of: {make, load, open} commands!", RED_TEXT(), BOLD()));
        }
        return true;
    }

    public boolean help() throws IOException {
        BufferedInputStream stream = new BufferedInputStream(Objects.requireNonNull(Main.class.getResourceAsStream("/help.txt")));
        byte[] text = new byte[2048];
        stream.read(text, 0, 2048);
        System.out.println(new String(text));


        return true;
    }
}


/*
* Building: Must build and run the artifact! Not the automatic Build & Run thingy on the right.
*   The correct artifact is called: SchoolProblemManager:jar
*
*   2023: Why didn't I just delete the incorrect artifact?
*   Either way, this sucks, it should be rewritten
* */