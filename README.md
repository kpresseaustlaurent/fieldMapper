# fieldMapper
Maps single or multiple fields from a template
Template:

```
{
    "Single": "{SingleLabel}",
    "Multiple": "{ListOfInfos~[Info1]/[Info2]}",
    "Nested" : "{NestedInfos~[firstSingle]/[secondSingle]([innerList~[name]:[value]])}"
}
```
Results
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
    ],
    "NestedInfos": [
        {
            "firstSingle": "ABBA",
            "secondSingle": "collection",
            "innerList": [
                {
                    "name": "Animal",
                    "value": "Wolf"
                },
                {
                    "name": "Animal2",
                    "value": "Dog"
                }
            ]
        },
        {
            "firstSingle": "ACDC",
            "secondSingle": "collection",
            "innerList": []
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

Output:
```
{
    "Single": "SingleField",
    "Multiple": "Bob/Gratton, Tim/Smith",
    "Nested" : "ABBA/collection(Animal:Wolf, Animal2:Dog), ACDC/collection"
}
```