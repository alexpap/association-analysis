Association Analysis
--------------------
Traditional Apriori Implementation.

* Loading latest MovieLens dataset ([here](http://grouplens.org/datasets/movielens/)) 
    - Data transformation into transactions (baskets) 
    - Supported data sets : ml_1m, ml_10m, ml_100k, ml_latest_small.
* Frequent Itemset Generation 
    - Traditional Apriori first Itemset generation as described [here](http://www-users.cs.umn.edu/~kumar/dmbook/ch6.pdf).
    - Apriori-gen using F_(k-1)XF_(k-1) Method
    - Support Counting using candidates hash trees.
    
* Association Rules Generation    

* Swing GUI, CLI support 
