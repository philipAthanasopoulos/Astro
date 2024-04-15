<h1>Searchio</h1>


<h1>About the project</h1>
This is a search engine application build to retrieve scientific articles. Users can enter keywords, authors, dates, etc. to get relevant results from a big database of scientific articles.
The search engine is implemented using the Lucene library and JavaFX on the frontend view.



<h1>The Dataset</h1>
The project uses the "All NeurIPS (NIPS) Papers" dataset which can be found at (https://www.kaggle.com/datasets/rowhitswami/nips-papers-1987-2019-updated/data?select=papers.csv). It consists of <b>9680</b> unique articles on the topic of <b>Neural Information Processing Systems</b>. Each dataset entry contains a year of publication, title, author details, abstracts and full text. These fields will be used to better determine the preferred articles to present to the user based on their search.


<h1>Search Engine Design</h1>

<h2>Visual aspects, UI and UX</h2>
The application consists of a main window frame. Within the windows exists a query input text field accompanied by
a search button. While the user types their query, some recommended autocompletions will be displayed
below the text field. The user can select any of them to automatically search for some results.

Upon committing the 
search input, the window is refreshed and results are rendered one under the other, while the query remains in the search bar.
Each result consists of an article title Label, followed by the post date of the article and a small summary of the article content.
Results that have been revisited will have their title color darker, so that the user knows they've visited that
article before.

By clicking on a desired result, the user is brought to a new tab where the full article is displayed. Any words or phrases in the article matching the search query will be 
underlined yellow color(marker). The user can search for other words or phrases in the file by clicking the "find in file" button.

![img.png](img.png)

<h2>Search Engine architecture</h2>




