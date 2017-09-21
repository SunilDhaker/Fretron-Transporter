package TransporterTest;

import com.fretron.Model.Command;

import java.util.List;

public class AssertClass {
    public static boolean assertThat(List<Command> list,int expectedRecords,String errorMessage) {
        if(list.size()==expectedRecords && list.get(list.size()-1).getErrorMessage()==null && errorMessage == null)
            return true;
        else if(list.size()==expectedRecords && list.get(list.size()-1).getErrorMessage().equalsIgnoreCase(errorMessage))
            return true;

        return false;
    }

    public static boolean assertTestActualData(List<Command> list,String expectedMessage,int expectedRecord )
    {
        int size=list.size();

        if (size==expectedRecord && list.get(size-1).getType().contains(expectedMessage)&& list.get(size-1).getStatusCode()==200)
        {
            return true;
        }
        else
        {
            return false;
        }


    }

}
