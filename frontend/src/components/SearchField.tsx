import { SyntheticEvent, useRef } from "react";

interface SearchFieldProps {
  title: string;
  defaultSearchTerm: string;
  inputId: string;
  onSearch: (searchTerm: string) => void;
}

export default function SearchField({
  title,
  defaultSearchTerm,
  inputId,
  onSearch,
}: SearchFieldProps) {
  const inputRef = useRef<HTMLInputElement>(null);

  const handleSave = (e: SyntheticEvent) => {
    e.preventDefault();
    if (inputRef.current) {
      onSearch(inputRef.current.value);
    }
  };

  return (
    <div>
      <form onSubmit={handleSave}>
        <label>
          {title}
          <input
            type="text"
            ref={inputRef}
            defaultValue={defaultSearchTerm}
            id={inputId}
            name={inputId}
          />
        </label>
        <button type="submit">Search</button>
      </form>
    </div>
  );
}
