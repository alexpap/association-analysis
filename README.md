Association Analysis
--------------------

* Loading MovieLens dataset
    - tranform data into transactions (baskets)
    - filter item key and title 
    
* Frequent Itemset Generation 
    - Traditional Apriori first Itemset generation as described above
        https://www.researchgate.net/publication/268512540_Review_of_Apriori_Based_Algorithms_on_Map_Reduce_Framework
    - K-Itemset generation - TODO

* Rule Generation - TODO

* Building the project

    ``` 
    java -version
    mvn -version
    mvn clean install -DskipTests
    mvn clean install 
    ```