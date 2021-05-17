public class link {
    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("link: usage: java link <filename> <pathname>");
            System.exit(1);
        }

        String filename = argv[0];
        String pathname = argv[1];

        Kernel.initialize();
        Kernel.link(filename, pathname);
        Kernel.exit(0);
    }
}
