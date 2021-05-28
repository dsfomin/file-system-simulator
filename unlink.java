public class unlink {
    public static void main(String[] argv) throws Exception {
        if (argv.length != 1) {
            System.err.println("link: usage: java unlink <pathname>");
            System.exit(1);
        }

        String pathname = argv[0];

        Kernel.initialize();
        Kernel.unlink(pathname);
        Kernel.exit(0);
    }
}
