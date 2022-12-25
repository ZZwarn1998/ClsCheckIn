package com.dachuang.util;

import java.io.IOException;

public class CmdRunner {
    private final static String CONDA_BAT = "D:\\Anaconda3\\Library\\bin\\conda.bat";
    private final static String PY_INTERPRETER_LOC = "D:\\Anaconda3\\envs\\cls\\python.exe";
    private final static String PY_CMD_LOC = "D:\\PCproj\\ClsCheckIn_py\\cmd.py";

    public static void runCommand(String[] cmd) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(cmd);
        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
        errorGobbler.start();
        StreamGobbler outGobbler = new StreamGobbler(proc.getInputStream(), "STDOUT");
        outGobbler.start();
        proc.waitFor();
    }

    public static String get_py_interpreter_loc(){
        return PY_INTERPRETER_LOC;
    }

    public static String get_py_cmd_loc(){
        return PY_CMD_LOC;
    }

    public static String get_conda_bat(){
        return CONDA_BAT;
    }
}
