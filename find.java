class find {
    public static String PROGRAM_NAME = "find";

    public static void main(String[] args) throws Exception {
        Kernel.initialize();

        if (args.length != 1) {
            System.out.println("Wrong \""+ PROGRAM_NAME + "\" command signature!");
            System.out.println("Usage: " + "java " + PROGRAM_NAME + " <path>");
            System.out.println("\t<path>" + " :\n\tPath to directory\n\tChecks if it exists\n\tOutputs list of directories and files in directory");
            Kernel.exit(1);
        }

        searchFiles(args[0]);
    }

    private static void searchFiles(String name) throws Exception {

        var stat = new Stat();
        var status = Kernel.stat(name, stat);

        if (status < 0) {
            Kernel.perror(PROGRAM_NAME);
            Kernel.exit(1);
        }

        var type = (short) (stat.getMode() & Kernel.S_IFMT);


        if (type == Kernel.S_IFREG) {
            System.out.println(name);
        }

        else if (type == Kernel.S_IFDIR) {
            System.out.println(name);

            int fd = Kernel.open(name, Kernel.O_RDONLY);

            if (fd < 0) {
                Kernel.perror(PROGRAM_NAME);
                System.out.println(PROGRAM_NAME + ": unable to open \"" + name + "\" for reading");
                Kernel.exit(1);
            } else {

                var directoryEntry = new DirectoryEntry();


                while (true) {

                    status = Kernel.readdir(fd, directoryEntry);
                    if (status <= 0)
                        break;

                    var entryName = directoryEntry.getName();

                    if (!entryName.equals(".") && !entryName.equals("..")) {
                        var fullName = getFullName(name, entryName);
                        searchFiles(fullName);
                    }
                }
            }
            Kernel.close(fd);
        }
    }

    private static String getFullName(String name, String entryName) {
        var stringBuilder = new StringBuilder(name);
        if (!name.equals("/")) {
            stringBuilder.append("/");
        }
        stringBuilder.append(entryName);

        return stringBuilder.toString();
    }
}