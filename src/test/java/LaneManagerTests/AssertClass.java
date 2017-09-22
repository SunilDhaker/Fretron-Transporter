package LaneManagerTests;

import com.fretron.Model.Command;

import java.util.List;

public class AssertClass {
    public static boolean assertThat(List<Command> list,int expectedRecords,String errorMessage) {
        if(list.size()==expectedRecords && errorMessage==null && list.get(list.size()-1).getErrorMessage()==null)
            return true;

        else if(list.size()==expectedRecords && (list.get(list.size()-1).getErrorMessage().equalsIgnoreCase(errorMessage)))
            return true;

        else
            return false;
    }
}
