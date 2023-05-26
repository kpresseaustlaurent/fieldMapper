# fieldMapper
Maps single or multiple fields from a template

### Template:

```
{
    "Single": "{SingleLabel}",
    "Multiple": "{ListOfInfos:[Info1]/[Info2]}"
}
```
### Results
```
{
    "SingleLabel": "SingleField",
    "ListOfInfos": [
        {
            "Info1": "Bob",
            "Info2": "Gratton"
        },
        {
            "Info1": "Tim",
            "Info2": "Smith"
        }
    ]
}
```
## Single Field Mapping
For single field mapping. The mapper will map the value of SingleLabel to the Single field

## Multiple Fields Mapping
For multiple field mapping. The mapper will look at the node at ListOfInfos node and get
all the values at the Info1 and Info2 fields and will zip them into a list formatted as the string
on the right of the colon (:)

### Output:
```
{
    "Single": "SingleField",
    "Multiple": "Bob/Gratton, Tim/Smith" 
}
```
