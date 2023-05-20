package pt.jorgeduarte.domain.libs;

import lombok.Getter;
import lombok.Setter;
import net.sf.saxon.s9api.XdmAtomicValue;

@Getter
@Setter
public class XQueryVariable{

    private String key;

    private XdmAtomicValue value;


    public void setIntValue(int value){
        this.value = new XdmAtomicValue(value);
    }

    public void setStringValue(String value){
        this.value = new XdmAtomicValue(value);
    }

    public void setDoubleValue(Double value){
        this.value = new XdmAtomicValue(value);
    }
}
