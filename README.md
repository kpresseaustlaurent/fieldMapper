# fieldMapper
Maps single or multiple fields from a template.

This algorithm currently makes 3 assumptions:
* The delimiters ({}, [], ~) are not used elsewhere. <sub> simple change if necessary</sub>
* Parallel values are either empty or completely filled. This means no partial results. <sub>medium change if necessary</sub>
* No nested single field mapping. <sub>medium change if necessary</sub>
## Single Field Mapping
For single field mapping. The mapper will map the value of the field inside curly braces to the field defined
in the template.

## Multiple Fields Mapping
For multiple field mapping. The mapper will look at the node with the field name that is on the left 
of the delimiter(~) and extract the fields on the right of the delimiter. Labels between square brackets([]) are 
extracted in parallel and are mapped to the string defined on the right of the delimiter.
```
#Ex: {somefield~This is a string that will be repeated for each zipped set [value1]/[value2]}
value1: a,c
value2: b,d
=> This is a string that will be repeated for each zipped set a/b, 
    This is a string that will be repeated for each zipped set c/d.
```
## Nested Fields Mapping
For nested field mapping, the mapper will look at the multiple field mappings inside a multiple field mapping.

## Example:

### Results:
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
### Template:

```
{
    "Single": "{SingleLabel}",
    "Multiple": "{ListOfInfos~[Info1]/[Info2]}",
    "Nested" : "{NestedInfos~[firstSingle]/[secondSingle]([innerList~[name]:[value]])}"
}
```
### Output:
```
{
    "Single": "SingleField",
    "Multiple": "Bob/Gratton, Tim/Smith",
    "Nested" : "ABBA/collection(Animal:Wolf, Animal2:Dog), ACDC/collection"
}
```
