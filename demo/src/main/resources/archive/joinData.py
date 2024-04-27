import pandas as pd

# Read the data from the CSV files
papers_df = pd.read_csv('papers.csv')
authors_df = pd.read_csv('authors.csv')

# Convert the fields to string
authors_df['first_name'] = authors_df['first_name'].astype(str)
authors_df['last_name'] = authors_df['last_name'].astype(str)
authors_df['institution'] = authors_df['institution'].astype(str)

# Group the authors DataFrame by 'source_id' and join all authors' names, surnames and institutions together
authors_grouped = authors_df.groupby('source_id').agg({
    'first_name': ', '.join,
    'last_name': ', '.join,
    'institution': ', '.join
}).reset_index()

# Merge the papers DataFrame with the grouped authors DataFrame
full_df = pd.merge(papers_df, authors_grouped, on='source_id')

# Write the resulting DataFrame to a new CSV file
full_df.to_csv('data.csv', index=False)