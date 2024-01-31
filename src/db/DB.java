package db;

import Model.CarIssueTM;
import Model.CarReturnTM;
import Model.CarsTM;
import Model.MemberTM;

import java.util.ArrayList;

public class DB {
    public static ArrayList<MemberTM> members = new ArrayList<>();
    public static ArrayList<CarsTM> cars = new ArrayList<>();
    public static ArrayList<CarIssueTM> issued = new ArrayList<>();
    public static ArrayList<CarReturnTM> returned = new ArrayList<>();
}
