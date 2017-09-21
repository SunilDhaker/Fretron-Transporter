package TransporterTest;

import com.fretron.Model.Command;

import java.util.List;

public class AssertClass {
    public static boolean assertThat(List<Command> list,int expectedRecords,String state) {
        if(list.size()==expectedRecords && list.get(list.size()-1).getType().equalsIgnoreCase(state))
            return true;

        return false;
    }
}
